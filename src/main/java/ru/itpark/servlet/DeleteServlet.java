package ru.itpark.servlet;

import ru.itpark.service.AutoService;
import ru.itpark.utils.Utils;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class DeleteServlet extends HttpServlet {
    private AutoService service;

    @Override
    public void init() {
        try {
            var context = new InitialContext();
            service = (AutoService) context.lookup(Utils.JNDI_LOOKUP_NAME);

        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (req.getPathInfo() != null) {
            String[] split = req.getPathInfo().split("/");
            if (split.length == 2) {
                var id = split[1];
                service.deleteById(id);
            }
        }

        resp.sendRedirect("/catalog");
    }
}
