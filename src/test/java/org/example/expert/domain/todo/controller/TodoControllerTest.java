package org.example.expert.domain.todo.controller;

import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.common.exception.NotFoundException;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.service.TodoService;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TodoController.class)
class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TodoService todoService;

    @Test
    void todo_단건_조회에_성공한다() throws Exception {
        // given
        long todoId = 1L;
        String title = "title";
        AuthUser authUser = new AuthUser(1L, "email", UserRole.USER, "nickname");
        User user = User.fromAuthUser(authUser);
        UserResponse userResponse = new UserResponse(user.getId(), user.getEmail());
        TodoResponse response = new TodoResponse(
                todoId,
                title,
                "contents",
                "Sunny",
                userResponse,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        // when
        when(todoService.getTodo(todoId)).thenReturn(response);

        // then
        mockMvc.perform(get("/todos/{todoId}", todoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(todoId))
                .andExpect(jsonPath("$.title").value(title));
    }

    @Test
    void todo_단건_조회_시_todo가_존재하지_않아_예외가_발생한다() throws Exception {
        // given
        long todoId = 1L;

//        // when
//        when(todoService.getTodo(todoId))
//                .thenThrow(new InvalidRequestException("Todo not found")); // 404 에러 상태 필요
//
//        // then
//        mockMvc.perform(get("/todos/{todoId}", todoId))
//                .andExpect(status().isBadRequest()) // isBadRequest == 400 상태 코드
//                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.name())) // 400 상태의 이름
//                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value())) // 400 상태의 코드
//                .andExpect(jsonPath("$.message").value("Todo not found")); // 예외 메세지


        // 67번줄~76번줄은 디버깅 해보았을 때 400이 나와야된다고 해서 bad request로 바꿔주었는데 NotFoundException을 만들어줘서 404로 나오게하면 어떨까 해서 실험? 해봄
        // when
        when(todoService.getTodo(todoId))
                .thenThrow(new NotFoundException("Todo not found")); // 404 에러 상태 필요

        // then
        mockMvc.perform(get("/todos/{todoId}", todoId))
                .andExpect(status().isNotFound()) // isNotFound == 404 상태 코드
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.name())) // 404 상태의 이름
                .andExpect(jsonPath("$.code").value(HttpStatus.NOT_FOUND.value())) // 404 상태의 코드
                .andExpect(jsonPath("$.message").value("Todo not found")); // 예외 메세지
    }
}
