package com.mysite.sbb.user;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateForm {

  @Column(columnDefinition = "boolean default true")
  private Boolean loginStatus;

  @NotEmpty(message = "기존 비밀번호는 필수항목입니다.")
  private String originPassword;

  @NotEmpty(message = "새 비밀번호는 필수항목입니다.")
  private String newPassword1;

  @NotEmpty(message = "비밀번호 확인은 필수항목입니다.")
  private String newPassword2;
}
