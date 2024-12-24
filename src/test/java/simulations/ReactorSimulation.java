package simulations;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

import io.gatling.javaapi.core.CoreDsl;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpDsl;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import java.time.Duration;

public class ReactorSimulation extends Simulation {

    // Protocol Definition
    HttpProtocolBuilder httpProtocol = HttpDsl.http
            .baseUrl("http://localhost:8080")
            .acceptHeader("application/json")
            .userAgentHeader("Gatling/Performance Test");


//    ScenarioBuilder scn = CoreDsl.scenario("Load Test Reactor")
//            .exec(http("reactor")
//                    .get("/api/reactor")
//                    .header("Content-Type", "application/json")
//                    .check(status().is(200))
//            );

    ScenarioBuilder scn = CoreDsl.scenario("Load Test Virtual Threads")
            .exec(http("virtual-thread")
                    .get("/api/virtual-thread")
                    .header("Content-Type", "application/json")
                    .check(status().is(200))
            );

    // Simulation
    public ReactorSimulation() {
        this.setUp(scn.injectOpen(
                rampUsersPerSec(0).to(600).during(Duration.ofSeconds(60)),
                constantUsersPerSec(600).during(Duration.ofSeconds(60)),
                rampUsersPerSec((600)).to(0).during(Duration.ofSeconds(60)))
        ).protocols(httpProtocol);
    }

}
