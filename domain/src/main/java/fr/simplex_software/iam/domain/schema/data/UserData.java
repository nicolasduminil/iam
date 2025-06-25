package fr.simplex_software.iam.domain.schema.data;

import java.util.*;

public record UserData(String username, String email, String firstName,
  String lastName, String password, List<String> roles)
{
}
