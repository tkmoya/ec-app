package com.tkmoya.springgradle.model;

import com.tkmoya.springgradle.model.LoginFormModel.First;
import com.tkmoya.springgradle.model.LoginFormModel.Second;
import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@GroupSequence({First.class, Second.class, LoginFormModel.class})
@Getter
@Setter
public class LoginFormModel {

    public interface First {
    }

    public interface Second {
    }

    @NotEmpty(message = "会員NOを入力してください", groups = {First.class})
    @Pattern(regexp = "^[0-9]+$", message = "会員NOは半角数字で入力してください", groups = {Second.class})
    private String memberNo;

    @NotEmpty(message = "パスワードを入力してください", groups = {First.class})
    private String password;
}
