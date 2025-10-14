package com.ecaservice.oauth.mapping;

import com.ecaservice.oauth.entity.PersonalAccessTokenEntity;
import com.ecaservice.web.dto.model.PersonalAccessTokenDto;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Personal access token mapper.
 *
 * @author Roman Batygin
 */
@Mapper
public interface PersonalAccessTokenMapper {

    /**
     * Maps personal access token entity to dto.
     *
     * @param personalAccessTokenEntity - personal access token entity
     * @return personal access token dto
     */
    PersonalAccessTokenDto map(PersonalAccessTokenEntity personalAccessTokenEntity);

    /**
     * Maps personal access token entities to dto list.
     *
     * @param personalAccessTokens - personal access token entities
     * @return personal access token dto list
     */
    List<PersonalAccessTokenDto> map(List<PersonalAccessTokenEntity> personalAccessTokens);

    @AfterMapping
    default void mapValid(PersonalAccessTokenEntity personalAccessTokenEntity,
                          @MappingTarget PersonalAccessTokenDto personalAccessTokenDto) {
        boolean valid = personalAccessTokenEntity.getExpireDate().isAfter(LocalDateTime.now());
        personalAccessTokenDto.setValid(valid);
    }
}
