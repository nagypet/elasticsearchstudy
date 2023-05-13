package hu.perit.elasticsearchstudy.model;

import lombok.Data;

@Data
public class SearchUserRequest
{
    private String firstName;

    private String lastName;
}
