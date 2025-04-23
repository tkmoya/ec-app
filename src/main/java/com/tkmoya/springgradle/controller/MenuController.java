package com.tkmoya.springgradle.controller;

import com.tkmoya.springgradle.exception.UserServiceException;
import com.tkmoya.springgradle.model.EditFormModel;
import com.tkmoya.springgradle.model.LoginFormModel;
import com.tkmoya.springgradle.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * メニュー画面と認証に関するコントローラー
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class MenuController {

    // セッション関連の定数
    public static final String SESSION_USER_ID = "userId";
    private static final String SESSION_USER_INFO = "editFormModel";
    private static final String SESSION_PREVIOUS_URL = "beforeEdit";

    // リダイレクト先の判定に使用するURLのパターン
    private static final String URL_EDIT_PATTERN = "/edit";
    private static final String URL_ORDER_END_PATTERN = "/order_end";

    private final UserService userService;

    /**
     * エラー画面遷移
     */
    @PostMapping("/error")
    public String showError() {
        return "menu";
    }

    /**
     * ルートパスへのアクセス時のメニュー画面遷移
     */
    @GetMapping("/")
    public String showIndex() {
        return "menu";
    }

    /**
     * メニュー画面遷移
     */
    @GetMapping("/menu")
    public String showMenuForm() {
        return "menu";
    }

    /**
     * ログイン画面遷移
     */
    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("loginFormModel", new LoginFormModel());
        return "login";
    }

    /**
     * ログイン処理
     *
     * @param loginFormModel ログインフォームモデル
     * @param result         バリデーション結果
     * @param model          画面モデル
     * @param request        HTTPリクエスト
     * @param session        HTTPセッション
     * @return 遷移先のビュー名
     */
    @PostMapping("/login")
    public String login(
            @Validated LoginFormModel loginFormModel,
            BindingResult result,
            Model model,
            HttpServletRequest request,
            HttpSession session
    ) {
        // サーバ側バリデーション
        if (result.hasErrors()) {
            return "login";
        }

        try {
            // ログイン認証処理
            userService.doLogin(loginFormModel);

            // ログイン成功時の処理
            int memberNo = Integer.parseInt(loginFormModel.getMemberNo());
            setupUserSession(session, memberNo);

            // 遷移元URLの保存
            saveRequestUrlIfNeeded(session, request);

            // 遷移先の決定
            return determineRedirectDestination(session);

        } catch (UserServiceException e) {
            log.warn("ログイン失敗: {}", loginFormModel.getMemberNo(), e);
            model.addAttribute("message", "ログインできませんでした。");
            return "login";
        } catch (Exception e) {
            log.error("ログイン処理中にエラーが発生しました", e);
            model.addAttribute("message", "システムエラーが発生しました。管理者にお問い合わせください。");
            return "login";
        }
    }

    /**
     * ログアウト処理
     *
     * @param session HTTPセッション
     * @return ログイン画面へのリダイレクト
     */
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        try {
            session.invalidate();
            log.info("ユーザーがログアウトしました");
        } catch (Exception e) {
            log.warn("セッション無効化中にエラーが発生しました", e);
        }
        return "redirect:login";
    }

    /**
     * ユーザーセッションの設定
     *
     * @param session  HTTPセッション
     * @param memberNo 会員番号
     */
    private void setupUserSession(HttpSession session, int memberNo) {
        // ユーザーIDをセッションに保存
        session.setAttribute(SESSION_USER_ID, memberNo);

        // ユーザー情報をセッションに保存
        EditFormModel editFormModel = userService.findUserAnsByMemberNo(memberNo);
        if (editFormModel != null) {
            session.setAttribute(SESSION_USER_INFO, editFormModel);
        }

        log.info("ユーザーがログインしました: {}", memberNo);
    }

    /**
     * リクエストURLの保存（必要な場合）
     *
     * @param session HTTPセッション
     * @param request HTTPリクエスト
     */
    private void saveRequestUrlIfNeeded(HttpSession session, HttpServletRequest request) {
        if (session.getAttribute(SESSION_PREVIOUS_URL) == null) {
            session.setAttribute(SESSION_PREVIOUS_URL, request.getRequestURI());
            log.debug("遷移元URLを保存しました: {}", request.getRequestURI());
        }
    }

    /**
     * リダイレクト先の決定
     *
     * @param session HTTPセッション
     * @return リダイレクト先のビュー名
     */
    private String determineRedirectDestination(HttpSession session) {
        String previousUrl = (String) session.getAttribute(SESSION_PREVIOUS_URL);

        if (previousUrl == null) {
            return "redirect:menu";
        }

        // 遷移元URLによってリダイレクト先を分岐
        if (URL_EDIT_PATTERN.equals(previousUrl)) {
            return "redirect:edit";
        } else if (URL_ORDER_END_PATTERN.equals(previousUrl)) {
            return "redirect:shopping_check";
        } else {
            return "redirect:menu";
        }
    }
}