package ru.itpark.servlet;

import ru.itpark.utils.Utils;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ImageServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (req.getPathInfo() != null) {
            String[] split = req.getPathInfo().split("/");
            if (split.length != 2) {
                throw new RuntimeException("are you kidding me?");
            }
            var id = split[1];
            var path = Paths.get(Utils.UPLOAD_DIR).resolve(id);
            if (!Files.exists(path)) {
                throw new RuntimeException("404");
            }

            Files.copy(path, resp.getOutputStream());

        }
    }
}
