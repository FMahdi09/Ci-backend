package Ci.Backend.Dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class LoginReponseDto
{
    private String refreshToken;

    private String accessToken;
}
