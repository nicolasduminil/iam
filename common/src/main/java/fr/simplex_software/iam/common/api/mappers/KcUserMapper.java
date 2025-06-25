package fr.simplex_software.iam.common.api.mappers;

import fr.simplex_software.iam.domain.schema.data.*;
import org.keycloak.representations.idm.*;
import org.mapstruct.*;
import org.mapstruct.factory.*;

@Mapper(componentModel = "default")
public interface KcUserMapper
{
  KcUserMapper INSTANCE = Mappers.getMapper(KcUserMapper.class);
  @Mapping(target = "credentials", ignore = true)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdTimestamp", ignore = true)
  UserRepresentation toRepresentation(UserData input);
}
