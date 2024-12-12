package ru.mirea.pkmn;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import ru.mirea.pkmn.entity.CardEntity;
import ru.mirea.pkmn.entity.StudentEntity;
import ru.mirea.pkmn.io.CardExport;
import ru.mirea.pkmn.io.CardImport;
import ru.mirea.pkmn.model.Card;
import ru.mirea.pkmn.network.PkmnHttpClient;
import ru.mirea.pkmn.repository.PkmnRepository;
import ru.mirea.pkmn.utils.ResourceFileLoader;

import java.util.List;
import java.util.UUID;



@SpringBootApplication
public class PkmnApplication {

    static ApplicationContext context;

    public static void main(String[] args) {

        context = SpringApplication.run(PkmnApplication.class, args);

        PkmnHttpClient pkmnHttpClient = context.getBean(PkmnHttpClient.class);

        PkmnRepository dbService = context.getBean(PkmnRepository.class);

        testNetwork(pkmnHttpClient);

        testDatabase(dbService);
    }

    public static void testNetwork(PkmnHttpClient client) {

        Logger logger = context.getBean(Logger.class); // Create logger

        ResourceFileLoader loader = context.getBean(ResourceFileLoader.class);

        Card cardFile = CardImport.parseCard(loader.getResourcePath("my_card.txt"));

        PkmnHttpClient.PokemonCardCallback callback = new PkmnHttpClient.PokemonCardCallback() {
            @Override
            public void onSuccess(JsonNode cardData) {

                logger.info("Card data: " + cardData); // Log data

                List<JsonNode> attacks = cardData.findValues("attacks"); // Find attacks section

                int ind = 0; // Index is needed to iterate over attack skills

                for (final JsonNode objNode : attacks) {
                    JsonNode text = objNode.findValue("text");

                    logger.info("Attack description: " + text.toString().replace('"', ' ').strip() );

                    assert cardFile != null;
                 //   cardFile.setSkillDescription(ind, text.toString());
                    ind++;
                }

                String EXPORT_PATH = "export.crd";

                CardExport.serializeCard(cardFile, EXPORT_PATH);

                CardImport.deserializeCard(loader.getResourcePath(EXPORT_PATH));

            }

            @Override
            public void onError(Throwable error) {
                logger.error("Failed to fetch card: " + error.getMessage());
            }
        };

        client.getPokemonCard("mewtwo", "130", callback);
    }

    public static void testDatabase(PkmnRepository service) {

        Logger logger = context.getBean(Logger.class); // Create logger

        ResourceFileLoader loader = context.getBean(ResourceFileLoader.class);

        Card card = CardImport.parseCard(loader.getResourcePath("my_card_test.txt"));

        service.saveCard(card.toEntity());

        StudentEntity student = service.getStudent("Belousov Daniil Olegovich");
        service.getCard(student);

        logger.info(student.toString());

        UUID uuid = UUID.fromString("05be7b93-17fa-3459-a0f4-c62f7628796d");
        CardEntity dCardEntity = service.getCard(uuid);

        logger.info(dCardEntity.toString());
    }
}