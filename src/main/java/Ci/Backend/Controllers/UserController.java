package Ci.Backend.Controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "api/users")
public class UserController
{
    @GetMapping
    public ResponseEntity<String> getUsers()
    {
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
