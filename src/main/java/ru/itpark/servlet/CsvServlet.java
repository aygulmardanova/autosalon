package ru.itpark.servlet;

import ru.itpark.service.AutoService;
import ru.itpark.utils.Utils;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

public class CsvServlet extends HttpServlet {
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
        String filename = "autos.csv";

        resp.setContentType("text/csv");
        resp.setHeader("Content-Disposition", "attachment;filename=" + filename);

        service.generateCsv(filename);

        ServletOutputStream os = resp.getOutputStream();
        FileInputStream is = new FileInputStream(filename);
        byte[] buffer = new byte[4096];
        int length;
        while ((length = is.read(buffer)) > 0){
            os.write(buffer, 0, length);
        }
        is.close();
        os.flush();
        os.close();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        var file = req.getPart("csv-file");
        service.createFromCsv(file);

        resp.sendRedirect("/catalog/");
    }

}
