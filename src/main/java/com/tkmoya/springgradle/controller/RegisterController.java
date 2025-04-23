package com.tkmoya.springgradle.controller;

import com.tkmoya.springgradle.model.RegisterFormModel;
import com.tkmoya.springgradle.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class RegisterController {

    private final UserService userService;

    @ModelAttribute("registerFormModel")
    public RegisterFormModel getRegisterForm() {
        return new RegisterFormModel();
    }

    /**
     * ユーザ登録画面遷移
     */
    @GetMapping("/register_user")
    public String showRegisterUserForm() {
        return "register_user";
    }

    /**
     * ユーザー登録処理開始
     */
    @PostMapping("/register_check")
    public String checkRegister(
            @Validated @ModelAttribute("registerFormModel") RegisterFormModel registerFormModel,
            BindingResult result,
            RedirectAttributes redirectAttr,
            Model model
    ) {

        // サーバ側バリデーション
        if (result.hasErrors()) {
            redirectAttr.addFlashAttribute("registerFormModel", result);
            redirectAttr.addFlashAttribute(BindingResult.MODEL_KEY_PREFIX + "registerFormModel", result);
            return "redirect:register_user";
        }

        model.addAttribute("registerFormModel", registerFormModel);
        return "register_check";
    }

    @PostMapping("/register_end")
    public String doRegister(@ModelAttribute RegisterFormModel registerFormModel, Model model) {

        try {
            // 登録処理
            userService.register(registerFormModel);
            // 登録成功
            model.addAttribute("registerFormModel", registerFormModel);
            return "register_end";
        } catch (Exception e) {
            // 登録失敗
            return "/WEB-INF/rg/rg_failure";
        }
    }
}
