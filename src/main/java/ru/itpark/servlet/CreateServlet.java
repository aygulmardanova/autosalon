package ru.itpark.servlet;

import ru.itpark.service.AutoService;
import ru.itpark.utils.Utils;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class CreateServlet extends HttpServlet {
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
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("colors", Utils.COLORS);
        req.getRequestDispatcher("/WEB-INF/create.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        var file = req.getPart("file");
        var name = req.getParameter("name");
        var year = Integer.valueOf(req.getParameter("year"));
        var color = req.getParameter("color");
        var power = Integer.valueOf(req.getParameter("power"));

        service.create(name, year, color, power, file);
        resp.sendRedirect("/catalog");
    }

}
