package io.backendscience.benchmarkreactorvirtualthreads.virtual.api.controller;

import io.backendscience.benchmarkreactorvirtualthreads.domain.Post;
import io.backendscience.benchmarkreactorvirtualthreads.domain.Todo;
import io.backendscience.benchmarkreactorvirtualthreads.domain.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Logger;

@RestController()
@RequestMapping("api")
public class VirtualThreadController {

    private final Logger logger = Logger.getLogger(VirtualThreadController.class.getName());
    private final ExecutorService executorService;
    private final WebClient webClient;

    public VirtualThreadController(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://jsonplaceholder.typicode.com").build();
        // Creating an executor service for virtual threads
        this.executorService = Executors.newVirtualThreadPerTaskExecutor();
    }

    @GetMapping("/virtual-thread")
    //public ResponseEntity<String> getApi() {
    public String getApi() {
        logger.info("Start Virtual Threads");

        try {
            // Creating virtual threads for each of the tasks
            Future<Todo> todoFuture = executorService.submit(this::fetchTodo);

            logger.info("Enviou Todo");

            Future<Post> postFuture = executorService.submit(this::fetchPost);

            logger.info("Enviou Post");

            Future<User> userFuture = executorService.submit(() -> fetchUser((Integer) todoFuture.get().userId()));

            logger.info("Enviou User");

            // Wait for the tasks to complete and get their results
            Todo todo = todoFuture.get();
            logger.info("Get Todo");

            Post post = postFuture.get();
            logger.info("Get Post");

            User user = userFuture.get();
            logger.info("Get User");

            logger.info("TODO: " + todo +
                    "\nPOST: " + post +
                    "\nUSER: " + user);

            // Combine results
            return "TODO: " + todo +
                    "\nUSER: " + user +
                    "\nPOST: " + post;
        } catch (Exception e) {
            e.printStackTrace();
            return "An error occurred: " + e.getMessage();
        }

    }

    private Todo fetchTodo() {
        return webClient.get()
                .uri("/todos/1")
                .retrieve()
                .bodyToMono(Todo.class)
                .block();  // block to fetch the result synchronously
    }

    private Post fetchPost() {
        return webClient.get()
                .uri("/posts/1")
                .retrieve()
                .bodyToMono(Post.class)
                .block();
    }

    private User fetchUser(Integer userId) {
        return webClient.get()
                .uri("/users/{id}", userId)
                .retrieve()
                .bodyToMono(User.class)
                .block();
    }

}
