package ru.itpark.servlet;

import ru.itpark.domain.Auto;
import ru.itpark.service.AutoService;
import ru.itpark.utils.Utils;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class CatalogServlet extends HttpServlet {
    private AutoService service;

    @Override
    public void init() {
        try {
            var context = new InitialContext();
            service = (AutoService) context.lookup(Utils.JNDI_LOOKUP_NAME);
            service.create("BMW", 2010, "white", 5000, null);
            service.create("BMW", 2015, "black", 500000, null);
            service.create("Volvo", 2012, "blue", 10000, null);
            service.create("Lada", 2004, "gold", 2000, null);

        } catch (NamingException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void destroy() {
        service.deleteAll();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("colors", Utils.COLORS);

        if (req.getPathInfo() != null) {
            String[] split = req.getPathInfo().split("/");
            if (split.length == 2) {
                var id = split[1];
                var auto = service.getById(id);
                req.setAttribute("item", auto);
                req.getRequestDispatcher("/WEB-INF/details.jsp").forward(req, resp);
                return;
            }
        }

        List<Auto> list;
        var q = req.getParameter("q");
        if (q != null && !"".equals(q)) {
            switch (req.getParameter("searchType")) {
                case "n":
                    list = service.findByName(q);
                    break;
                case "c":
                    list = service.findByColor(q);
                    break;
                case "y":
                    list = service.findByYear(Integer.valueOf(q));
                    break;
                case "p":
                    list = service.findByPower(Integer.valueOf(q));
                    break;
                default:
                    list = Collections.emptyList();
            }
        } else {
            list = service.getAll();
        }

        if (list.isEmpty())
            req.setAttribute("emptyMessage", "No autos");
        req.setAttribute("items", list);
        req.getRequestDispatcher("/WEB-INF/catalog.jsp").forward(req, resp);
    }

}
