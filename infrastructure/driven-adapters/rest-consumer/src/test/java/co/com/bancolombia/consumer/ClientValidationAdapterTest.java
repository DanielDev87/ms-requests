package co.com.bancolombia.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;

class ClientValidationAdapterTest {

    private static MockWebServer mockBackEnd;
    private ClientValidationAdapter clientValidationAdapter;
    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeAll
    static void setUp() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockBackEnd.shutdown();
    }

    @BeforeEach
    void initialize() {
        String baseUrl = String.format("http://localhost:%s", mockBackEnd.getPort());
        WebClient webClient = WebClient.builder().baseUrl(baseUrl).build();
        clientValidationAdapter = new ClientValidationAdapter(webClient);
    }

    @Test
    @DisplayName("Debería retornar el ID del cliente cuando el usuario existe")
    void shouldReturnClientIdWhenUserExists() throws JsonProcessingException {
        // Arrange: Preparamos la respuesta simulada
        String documentNumber = "12345";
        UserDTO mockUserResponse = new UserDTO(101L);

        mockBackEnd.enqueue(new MockResponse()
                .setBody(mapper.writeValueAsString(mockUserResponse))
                .setResponseCode(200)
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        // Act: Ejecutamos el método que queremos probar
        Mono<Long> result = clientValidationAdapter.findClientIdByDocumentNumber(documentNumber);

        // Assert: Verificamos el resultado
        StepVerifier.create(result)
                .expectNext(101L)
                .verifyComplete();
    }

    @Test
    @DisplayName("Debería retornar un Mono vacío cuando el usuario no existe (404)")
    void shouldReturnEmptyWhenUserDoesNotExist() {
        // Arrange
        String documentNumber = "99999";

        // Simulamos una respuesta 404 Not Found
        mockBackEnd.enqueue(new MockResponse().setResponseCode(404));

        // Act
        Mono<Long> result = clientValidationAdapter.findClientIdByDocumentNumber(documentNumber);

        // Assert
        StepVerifier.create(result)
                .expectNextCount(0)
                .verifyComplete();
    }
}