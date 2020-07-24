package com.ecaservice.oauth.controller;

import com.ecaservice.oauth.TestHelperUtils;
import com.ecaservice.oauth.dto.CreateUserDto;
import com.ecaservice.oauth.entity.UserEntity;
import com.ecaservice.oauth.mapping.RoleMapperImpl;
import com.ecaservice.oauth.mapping.UserMapper;
import com.ecaservice.oauth.mapping.UserMapperImpl;
import com.ecaservice.oauth.repository.UserEntityRepository;
import com.ecaservice.oauth.service.PasswordService;
import com.ecaservice.oauth.service.UserService;
import com.ecaservice.web.dto.model.PageDto;
import com.ecaservice.web.dto.model.PageRequestDto;
import com.ecaservice.web.dto.model.UserDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;

import static com.ecaservice.oauth.TestHelperUtils.createUserEntity;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for checking {@link UserController} functionality.
 *
 * @author Roman Batygin
 */
@WebMvcTest(controllers = UserController.class,
        useDefaultFilters = false,
        includeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = UserController.class)
        })
@AutoConfigureMockMvc(addFilters = false)
@Import({UserMapperImpl.class, RoleMapperImpl.class})
class UserControllerTest {

    private static final String BASE_URL = "/users";
    private static final String CREATE_URL = BASE_URL + "/create";
    private static final String LIST_URL = BASE_URL + "/list";

    private static final String PAGE_PARAM = "page";
    private static final String SIZE_PARAM = "size";

    private static final long TOTAL_ELEMENTS = 1L;
    private static final int PAGE_NUMBER = 0;

    @MockBean
    private UserService userService;
    @MockBean
    private PasswordService passwordService;
    @Inject
    private UserMapper userMapper;
    @MockBean
    private ApplicationEventPublisher applicationEventPublisher;
    @MockBean
    private UserEntityRepository userEntityRepository;

    @Inject
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testCreateUser() throws Exception {
        CreateUserDto createUserDto = TestHelperUtils.createUserDto();
        UserEntity userEntity = createUserEntity();
        UserDto expected = userMapper.map(userEntity);
        when(userService.createUser(any(CreateUserDto.class), any())).thenReturn(userEntity);
        mockMvc.perform(post(CREATE_URL)
                .content(objectMapper.writeValueAsString(createUserDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expected)));
    }
    @Test
    void testGetUsersPage() throws Exception {
        Page<UserEntity> userEntityPage = Mockito.mock(Page.class);
        when(userEntityPage.getTotalElements()).thenReturn(TOTAL_ELEMENTS);
        List<UserEntity> userEntityList = Collections.singletonList(createUserEntity());
        PageDto<UserDto> expected = PageDto.of(userMapper.map(userEntityList), PAGE_NUMBER, TOTAL_ELEMENTS);
        when(userEntityPage.getContent()).thenReturn(userEntityList);
        when(userService.getNextPage(any(PageRequestDto.class))).thenReturn(userEntityPage);
        mockMvc.perform(get(LIST_URL)
                .param(PAGE_PARAM, String.valueOf(PAGE_NUMBER))
                .param(SIZE_PARAM, String.valueOf(TOTAL_ELEMENTS)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expected)));
    }

}
