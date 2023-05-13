package hu.perit.elasticsearchstudy.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserResponse {

   private String uuid;
   private String firstName;
   private String lastName;
   private String email;
}
