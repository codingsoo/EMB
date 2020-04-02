package em.embedded.br.com.codenation;


import br.com.codenation.hospital.GestaohospitalarApplication;
import com.mongodb.MongoClient;
import com.p6spy.engine.spy.P6SpyDriver;
import org.evomaster.client.java.controller.AuthUtils;
import org.evomaster.client.java.controller.EmbeddedSutController;
import org.evomaster.client.java.controller.InstrumentedSutStarter;
import org.evomaster.client.java.controller.problem.ProblemInfo;
import org.evomaster.client.java.controller.problem.RestProblem;
import org.evomaster.client.java.controller.api.dto.AuthenticationDto;
import org.evomaster.client.java.controller.api.dto.SutInfoDto;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.GenericContainer;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Class used to start/stop the SUT. This will be controller by the EvoMaster process
 */
public class EmbeddedEvoMasterController extends EmbeddedSutController {

    public static void main(String[] args) {

        int port = 40100;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }

        EmbeddedEvoMasterController controller = new EmbeddedEvoMasterController(port);
        InstrumentedSutStarter starter = new InstrumentedSutStarter(controller);

        starter.start();
    }


    private ConfigurableApplicationContext ctx;
    private MongoClient mongoClient;


    private static final GenericContainer mongodb = new GenericContainer("mongo:3.2")
            .withExposedPorts(27017);


    public EmbeddedEvoMasterController() {
        this(0);
    }

    public EmbeddedEvoMasterController(int port) {
        setControllerPort(port);
    }


    @Override
    public String startSut() {

        mongodb.start();

        mongoClient = new MongoClient(mongodb.getContainerIpAddress(),
                mongodb.getMappedPort(27017));

        ctx = SpringApplication.run(GestaohospitalarApplication.class,
                new String[]{"--server.port=0",
                        "--liquibase.enabled=false",
                        "--spring.data.mongodb.uri=mongodb://" + mongodb.getContainerIpAddress() + ":" + mongodb.getMappedPort(27017) + "/HospitalDB",
                        "--spring.cache.type=NONE"
                });


        return "http://localhost:" + getSutPort();
    }

    protected int getSutPort() {
        return (Integer) ((Map) ctx.getEnvironment()
                .getPropertySources().get("server.ports").getSource())
                .get("local.server.port");
    }


    @Override
    public boolean isSutRunning() {
        return ctx != null && ctx.isRunning();
    }

    @Override
    public void stopSut() {
        ctx.stop();
        ctx.close();

        mongodb.stop();
    }

    @Override
    public String getPackagePrefixesToCover() {
        return "br.com.codenation.hospital.";
    }

    @Override
    public void resetStateOfSUT() {
        mongoClient.getDatabase("HospitalDB").drop();
        mongoClient.getDatabase("HospitalDB-shadow").drop();
    }


    @Override
    public List<AuthenticationDto> getInfoForAuthentication() {
        return null;
    }

    @Override
    public Connection getConnection() {
        return null;
    }

    @Override
    public String getDatabaseDriverName() {
        return null;
    }

    @Override
    public ProblemInfo getProblemInfo() {
        return new RestProblem(
                "http://localhost:" + getSutPort() + "/v2/api-docs",
                null
        );
    }

    @Override
    public SutInfoDto.OutputFormat getPreferredOutputFormat() {
        return SutInfoDto.OutputFormat.JAVA_JUNIT_4;
    }
}
