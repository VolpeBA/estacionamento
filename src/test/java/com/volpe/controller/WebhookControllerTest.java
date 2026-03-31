package com.volpe.controller;

import com.volpe.exception.NotFoundException;
import com.volpe.exception.SectorFullException;
import com.volpe.service.WebhookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WebhookController.class)
class WebhookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WebhookService webhookService;

    @Test
    void shouldReturnOkOnEntryEvent() throws Exception {
        mockMvc.perform(post("/webhook")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"license_plate":"ZUL0001","event_type":"ENTRY","entry_time":"2025-01-01T12:00:00Z"}
                                """))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnOkOnParkedEvent() throws Exception {
        mockMvc.perform(post("/webhook")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"license_plate":"ZUL0001","event_type":"PARKED","lat":-23.561684,"lng":-46.655981}
                                """))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnOkOnExitEvent() throws Exception {
        mockMvc.perform(post("/webhook")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"license_plate":"ZUL0001","event_type":"EXIT","exit_time":"2025-01-01T13:00:00Z"}
                                """))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnConflictWhenSectorIsFull() throws Exception {
        doThrow(new SectorFullException("A")).when(webhookService).process(any());

        mockMvc.perform(post("/webhook")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"license_plate":"ZUL0001","event_type":"ENTRY","entry_time":"2025-01-01T12:00:00Z"}
                                """))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldReturnNotFoundWhenSessionDoesNotExist() throws Exception {
        doThrow(new NotFoundException("No active session")).when(webhookService).process(any());

        mockMvc.perform(post("/webhook")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"license_plate":"ZUL9999","event_type":"EXIT","exit_time":"2025-01-01T13:00:00Z"}
                                """))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnBadRequestWhenLicensePlateIsMissing() throws Exception {
        mockMvc.perform(post("/webhook")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"event_type":"ENTRY","entry_time":"2025-01-01T12:00:00Z"}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenEventTypeIsMissing() throws Exception {
        mockMvc.perform(post("/webhook")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"license_plate":"ZUL0001","entry_time":"2025-01-01T12:00:00Z"}
                                """))
                .andExpect(status().isBadRequest());
    }
}
