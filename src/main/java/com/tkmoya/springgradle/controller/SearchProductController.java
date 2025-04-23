package com.tkmoya.springgradle.controller;

import com.tkmoya.springgradle.model.CategoryModel;
import com.tkmoya.springgradle.model.SearchProductModel;
import com.tkmoya.springgradle.model.SearchProductResultModel;
import com.tkmoya.springgradle.service.FormInitService;
import com.tkmoya.springgradle.service.ProductService;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.TreeMap;

@Controller
@RequiredArgsConstructor
public class SearchProductController {
    // Session attributes
    private static final String CATEGORY_LIST = "categoryList";
    private static final String SEARCH_PRODUCT_MODEL = "searchProductModel";
    private static final String PRODUCT_PAGE_LIST = "productPageList";
    public static final String PRODUCT = "selectProductMap";

    // Error messages
    private static final String ERROR_SELECT_PRODUCT = "注文商品を選択してください";
    private static final String ERROR_SELECT_QUANTITY = "数量を選択してください";
    private static final String ERROR_NUMERIC_QUANTITY = "購入数は半角数字で入力してください。";
    private static final String ERROR_QUANTITY_RANGE = "購入数は1～999の数値で入力してください。";
    private static final String ERROR_INSUFFICIENT_STOCK = "在庫が足りません。購入数を変更してください。";
    private static final String ERROR_STOCK_SHORTAGE = "在庫が%s不足しています";

    private final FormInitService formInitService;
    private final ProductService productService;
    private final ServletContext application;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        // Convert empty strings to null
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    @GetMapping("/order_start")
    public String showInitialSearchForm(Model model, HttpSession session) {
        initializeCategoryList(model);
        model.addAttribute(SEARCH_PRODUCT_MODEL, new SearchProductModel());
        return "search_product";
    }

    @PostMapping("/search_product")
    public String searchProducts(
            @Validated @ModelAttribute SearchProductModel searchProduct,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            HttpSession session
    ) {
        if (result.hasErrors()) {
            return "search_product";
        }

        redirectAttributes.addFlashAttribute(SEARCH_PRODUCT_MODEL, searchProduct);
        session.setAttribute(SEARCH_PRODUCT_MODEL, searchProduct);

        List<List<SearchProductResultModel>> productPageList = productService.searchProduct(searchProduct);
        session.setAttribute(PRODUCT_PAGE_LIST, productPageList);

        return "redirect:/search_product?pageCnt=1";
    }

    @GetMapping("/search_product")
    public String displayProductsPage(
            Model model,
            @RequestParam("pageCnt") int pageCnt,
            HttpSession session
    ) {
        initializeCategoryList(model);
        session.removeAttribute("error");
        SearchProductModel searchProductModel = (SearchProductModel) session.getAttribute(SEARCH_PRODUCT_MODEL);
        model.addAttribute(SEARCH_PRODUCT_MODEL, searchProductModel);

        List<List<SearchProductResultModel>> productPageList =
                (List<List<SearchProductResultModel>>) session.getAttribute(PRODUCT_PAGE_LIST);

        if (productPageList == null) {
            return "redirect:/order_start";
        }

        pageCnt = validatePageCount(pageCnt, productPageList.size());
        List<SearchProductResultModel> viewPage = productPageList.get(pageCnt - 1);

        if (viewPage.isEmpty()) {
            session.setAttribute("error", "条件に該当する商品は0件です。");
            return "redirect:/order_start";
        }

        model.addAttribute("viewPage", viewPage);
        model.addAttribute("pageCnt", pageCnt);
        model.addAttribute("totalPage", productPageList.size());

        return "search_product";
    }

    @PostMapping("/select_product")
    public String selectProduct(
            @Valid @ModelAttribute SearchProductResultModel searchProductResultModel,
            BindingResult result,
            Model model,
            HttpServletRequest request,
            HttpSession session
    ) {
        String[] selectedProducts = request.getParameterValues("selectProductCode");

        if (selectedProducts == null) {
            request.setAttribute("error", ERROR_SELECT_PRODUCT);
            return displayProductsPage(model, 1, session);
        }

        TreeMap<String, SearchProductResultModel> selectProductMap = getOrCreateCartMap(session);

        for (String productCode : selectedProducts) {
            SearchProductResultModel product = productService.searchProductByCode(productCode);
            String quantityInput = request.getParameterValues(productCode)[0];

            if (!isValidQuantityInput(quantityInput, request)) {
                return displayProductsPage(model, 1, session);
            }

            int quantity = Integer.parseInt(quantityInput);

            if (selectProductMap.containsKey(productCode)) {
                SearchProductResultModel existingProduct = selectProductMap.get(productCode);
                int currentQuantity = Integer.parseInt(existingProduct.getProductCnt());
                int newQuantity = currentQuantity + quantity;

                if (!isValidQuantityRange(newQuantity, request) ||
                        !isStockSufficient(newQuantity, product.getStockCount(), request)) {
                    return displayProductsPage(model, 1, session);
                }

                product.setProductCnt(String.valueOf(newQuantity));
            } else {
                if (!isValidQuantityRange(quantity, request) ||
                        !isStockSufficient(quantity, product.getStockCount(), request)) {
                    return displayProductsPage(model, 1, session);
                }

                product.setProductCnt(quantityInput);
            }

            selectProductMap.put(product.getProductCode(), product);
        }

        session.setAttribute(PRODUCT, selectProductMap);
        return "add_cart";
    }

    @GetMapping("/product_detail")
    public String showProductDetail(
            Model model,
            @RequestParam("productCode") String productCode
    ) {
        SearchProductResultModel product = productService.searchProductByCode(productCode);
        model.addAttribute("code", product);
        return "product_detail";
    }

    @PostMapping("/product_detail")
    public String addFromProductDetail(
            Model model,
            HttpServletRequest request,
            HttpSession session
    ) {
        String[] detailCodes = request.getParameterValues("detailCode");
        if (detailCodes == null || detailCodes.length == 0) {
            return "redirect:/cart_list";
        }

        TreeMap<String, SearchProductResultModel> selectProductMap = getOrCreateCartMap(session);
        String detailCode = detailCodes[0];

        SearchProductResultModel product = productService.searchProductByCode(detailCode);
        String quantityInput = request.getParameterValues(detailCode)[0];

        if (!isValidDetailQuantityInput(quantityInput, product, request, model)) {
            return "product_detail";
        }

        int quantity = Integer.parseInt(quantityInput);

        if (selectProductMap.containsKey(detailCode)) {
            SearchProductResultModel existingProduct = selectProductMap.get(detailCode);
            int currentQuantity = Integer.parseInt(existingProduct.getProductCnt());
            int newQuantity = currentQuantity + quantity;

            if (!isValidStockForDetail(newQuantity, product, request, model)) {
                return "product_detail";
            }

            product.setProductCnt(String.valueOf(newQuantity));
        } else {
            if (!isValidStockForDetail(quantity, product, request, model)) {
                return "product_detail";
            }

            product.setProductCnt(quantityInput);
        }

        selectProductMap.put(product.getProductCode(), product);
        session.setAttribute(PRODUCT, selectProductMap);

        return "redirect:/cart_list";
    }

    // Helper methods
    private void initializeCategoryList(Model model) {
        List<CategoryModel> categoryList;

        if (application.getAttribute(CATEGORY_LIST) == null) {
            // カテゴリリストがない場合はDBから取得して保存
            categoryList = formInitService.getCategoryChoices();
            application.setAttribute(CATEGORY_LIST, categoryList);
        } else {
            // 既に存在する場合は取得
            categoryList = (List<CategoryModel>) application.getAttribute(CATEGORY_LIST);
        }

        // どちらの場合もモデルに追加
        model.addAttribute("categoryList", categoryList);
    }

    private int validatePageCount(int pageCnt, int totalPages) {
        if (pageCnt < 1) {
            return 1;
        } else if (pageCnt > totalPages) {
            return totalPages;
        }
        return pageCnt;
    }

    @SuppressWarnings("unchecked")
    private TreeMap<String, SearchProductResultModel> getOrCreateCartMap(HttpSession session) {
        Object cartSession = session.getAttribute(PRODUCT);
        return (cartSession != null) ?
                (TreeMap<String, SearchProductResultModel>) cartSession :
                new TreeMap<>();
    }

    private boolean isValidQuantityInput(String quantityInput, HttpServletRequest request) {
        if (quantityInput == null || quantityInput.isEmpty()) {
            request.setAttribute("error", ERROR_SELECT_QUANTITY);
            return false;
        }

        if (!quantityInput.matches("[0-9]+")) {
            request.setAttribute("error", ERROR_NUMERIC_QUANTITY);
            return false;
        }

        return true;
    }

    private boolean isValidQuantityRange(int quantity, HttpServletRequest request) {
        if (quantity < 1 || quantity > 999) {
            request.setAttribute("error", ERROR_QUANTITY_RANGE);
            return false;
        }
        return true;
    }

    private boolean isStockSufficient(int quantity, String stockStr, HttpServletRequest request) {
        int stock = Integer.parseInt(stockStr);
        if (stock < quantity) {
            request.setAttribute("error", ERROR_INSUFFICIENT_STOCK);
            return false;
        }
        return true;
    }

    private boolean isValidDetailQuantityInput(
            String quantityInput,
            SearchProductResultModel product,
            HttpServletRequest request,
            Model model
    ) {
        if (quantityInput == null || quantityInput.isEmpty()) {
            request.setAttribute("error", ERROR_SELECT_QUANTITY);
            model.addAttribute("code", product);
            return false;
        }

        if (!quantityInput.matches("[0-9]+")) {
            request.setAttribute("error", ERROR_NUMERIC_QUANTITY);
            model.addAttribute("code", product);
            return false;
        }

        int quantity = Integer.parseInt(quantityInput);
        if (quantity < 1 || quantity > 999) {
            request.setAttribute("error", ERROR_QUANTITY_RANGE);
            model.addAttribute("code", product);
            return false;
        }

        return true;
    }

    private boolean isValidStockForDetail(
            int quantity,
            SearchProductResultModel product,
            HttpServletRequest request,
            Model model
    ) {
        int stock = Integer.parseInt(product.getStockCount());
        if (stock < quantity) {
            String shortage = String.valueOf(Math.abs(stock - quantity));
            request.setAttribute("error", String.format(ERROR_STOCK_SHORTAGE, shortage));
            model.addAttribute("code", product);
            return false;
        }
        return true;
    }
}