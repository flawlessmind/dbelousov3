package ru.mirea.pkmn.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import ru.mirea.pkmn.entity.StudentEntity;
import ru.mirea.pkmn.repository.PkmnRepository;
import ru.mirea.pkmn.entity.CardEntity;
import ru.mirea.pkmn.repository.PkmnRepositoryImpl;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;


@WebServlet(urlPatterns = "/cards/*", loadOnStartup = 1)
public class CardServlet extends HttpServlet {

    PkmnRepository pkmnRepository;

    Logger logger;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        // Retrieve the EntityManager from the servlet context
        EntityManager em = (EntityManager) config.getServletContext().getAttribute("entityManager");

        if (em != null) {
            pkmnRepository = new PkmnRepositoryImpl(em, logger);
            logger.info("PkmnRepository initialized successfully");
        } else {
            logger.error("EntityManager is null in CardServlet");
            throw new ServletException("Failed to initialize CardServlet: EntityManager is null");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();

        CardEntity card = null;

        if (req.getParameter("name") != null) {

            String cardName = req.getParameter("name");
            card = pkmnRepository.getCard(cardName);

        } else if (req.getParameter("id") != null) {

            UUID cardId = UUID.fromString(req.getParameter("id"));
            card = pkmnRepository.getCard(cardId);

        } else if (req.getParameter("owner") != null) {

            StudentEntity student = pkmnRepository.getStudent(req.getParameter("owner"));
            card = pkmnRepository.getCard(student);

        } else {

            resp.setStatus(400);
            out.write("""
            {
                "error": "Bad Request",
                "message": "No valid parameters passed"
            }
            """);

            return;
        }

        ObjectMapper objectMapper = new ObjectMapper();

        if (card != null) {
            out.write(objectMapper.writeValueAsString(card));
        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            out.write("{\"error\": \"Card not found\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        PrintWriter out = resp.getWriter();

        resp.setContentType("application/json");

        CardEntity card = objectMapper.readValue(req.getInputStream(), CardEntity.class);

        if (card.getPokemonOwner() == null) {

            resp.setStatus(400);
            out.write("""
            {
                "error": "Bad Request",
                "message": "A card must have a pokemon owner before it can be saved."
            }
            """);

            return;
        }

        if (pkmnRepository.cardExists(card.getName())) {

            resp.setStatus(HttpServletResponse.SC_CONFLICT);
            out.write("""
            {
                "error": "Conflict",
                "message": "A card with this name already exists in the database."
            }
            """);

            return;
        }

        pkmnRepository.saveCard(card);

        out.write(objectMapper.writeValueAsString(card));
        resp.setStatus(HttpServletResponse.SC_CREATED);
    }
}
