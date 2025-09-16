package com.demo.dms.web.dto;

import com.demo.dms.entity.UserAccount;

public record UserDto(int userId, String fullName, String email, String role) {
  public static UserDto from(UserAccount u) {
    return new UserDto(u.getUserId(), u.getFullName(), u.getEmail(), u.getRole());
  }
}
