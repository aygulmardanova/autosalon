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
import java.util.UUID;

public class UpdateServlet extends HttpServlet {
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
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String[] split = req.getPathInfo().split("/");
        if (split.length == 2) {
            var id = split[1];
            var auto = service.getById(id);
            auto.setName(req.getParameter("name"));
            auto.setYear(Integer.valueOf(req.getParameter("year")));
            auto.setColor(req.getParameter("color"));
            auto.setPower(Integer.valueOf(req.getParameter("power")));

            var file = req.getPart("file");
            var fileName = file != null
                    ? UUID.randomUUID().toString()
                    : null;
            if (file != null)
                file.write(fileName);

            auto.setImage(fileName);
            service.updateById(auto);
        }

        resp.sendRedirect("/catalog");
    }

}
