package io.backendscience.benchmarkreactorvirtualthreads.reactor.api.controller;

import io.backendscience.benchmarkreactorvirtualthreads.domain.Post;
import io.backendscience.benchmarkreactorvirtualthreads.domain.Todo;
import io.backendscience.benchmarkreactorvirtualthreads.domain.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.logging.Logger;

@RestController
@RequestMapping("api")
public class ReactorController {

    private final WebClient webClient;
    private final Logger logger = Logger.getLogger(ReactorController.class.getName());

    public ReactorController(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://jsonplaceholder.typicode.com").build();
    }

    @GetMapping("reactor")
    public Mono<String> getReactorResponse() {

        logger.info("Start Reactor");

        return webClient
                .get()
                .uri("/todos/1")
                .retrieve()
                .bodyToMono(Todo.class)
                .onErrorResume(e -> {
                    // Handle Todo not found (404) or other errors
                    return Mono.error(new RuntimeException("Todo not found or unavailable"));
                })
                .flatMap(todo -> {
                    logger.info("Respondeu Todo e enviou user");

                    // Fetch User based on userId from Todo
                    Mono<User> userMono = webClient
                            .get()
                            .uri("/users/{id}", todo.userId())
                            .retrieve()
                            .bodyToMono(User.class)
                            .onErrorResume(e -> Mono.error(new RuntimeException("User not found or unavailable")));

                    logger.info("Enviou posts");

                    // Fetch Post (this can happen independently)
                    Mono<Post> postMono = webClient
                            .get()
                            .uri("/posts/1")
                            .retrieve()
                            .bodyToMono(Post.class)
                            .onErrorResume(e -> Mono.error(new RuntimeException("Post not found or unavailable")));

                    logger.info("Passou pelo return");

                    // Combine Todo, User, and Post
                    return Mono.zip(userMono, postMono)
                            .map(tuple -> {
                                User user = tuple.getT1();
                                Post post = tuple.getT2();

                                logger.info("TODO: " + todo +
                                        "\nPOST: " + post +
                                        "\nUSER: " + user);

                                return "TODO: " + todo +
                                        "\nUSER: " + user +
                                        "\nPOST: " + post;
                            });
                })
                .onErrorResume(e -> {
                    logger.info("An error occurred: " + e.getMessage());
                    // Final fallback for any errors in the pipeline
                    return Mono.just("An error occurred: " + e.getMessage());
                });
    }

//    @GetMapping("reactor")
//    public Mono<String> getReactorResponse() {
//
//
//
////        Mono<Todo> todoMono = webClient
////                .get()
////                .uri("/todos/1")
////                .retrieve()
////                .bodyToMono(Todo.class);
//
//        Todo todoMono = webClient
//                .get()
//                .uri("/todos/1")
//                .retrieve()
//                .bodyToMono(Todo.class)
//                .block();
//
//        logger.info("Passou por todos.");
//
//        Mono<Post> postMono = webClient
//                .get()
//                .uri("/posts/1")
//                .retrieve()
//                .bodyToMono(Post.class);
//
//        logger.info("Passou por posts.");
//
//        Mono<User> userMono = webClient
//                .get()
//                .uri("/users/1")
//                .retrieve()
//                .bodyToMono(User.class);
//
//        logger.info("passou por users.");
//
////        return Mono.zip(todoMono, postMono, userMono)
////                .map(tuple -> {
////                    Todo todo = tuple.getT1();
////                    Post post = tuple.getT2();
////                    User user = tuple.getT3();
////
////                    logger.info("TODO: " + todo +
////                            "\nPOST: " + post +
////                            "\nUSER: " + user);
////
////                    return "TODO: " + todo +
////                            "\nPOST: " + post +
////                            "\nUSER: " + user;
////                });
//
//        return Mono.just("Hello from Reactor!");
//    }
}