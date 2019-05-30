<%@ page import="ru.itpark.domain.Auto" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>Document</title>
    <%@ include file="bootstrap.jsp" %>
</head>
<body>
<div class="container">

    <% if (request.getAttribute("item") != null) { %>
    <% Auto item = (Auto) request.getAttribute("item"); %>
    <div class="row pl-0 pr-0">
        <h1 class="col align-self-center text-center"><%= item.getName() %>
        </h1>

        <div class="col-sm-6 mt-3">
            <div class="card">
                <img src="<%= request.getContextPath() %>/images/<%= item.getImage() %>" class="card-img-top">
            </div>
        </div>
    </div>
    <div class="row mt-5">

    </div>
    <div class="row mt-1">
        <div class="col col-sm-4">
            <div class="input-group mb-3">
                <div class="input-group-prepend">
                    <span class="input-group-text">Color</span>
                </div>
                <output class="form-control"><%= item.getColor() %>
                </output>
            </div>
        </div>
        <div class="col col-sm-4">
            <div class="input-group mb-3">
                <div class="input-group-prepend">
                    <span class="input-group-text">Year</span>
                </div>
                <output class="form-control"><%= item.getYear() %>
                </output>
            </div>
        </div>
        <div class="col col-sm-4">
            <div class="input-group mb-3">
                <div class="input-group-prepend">
                    <span class="input-group-text">Power</span>
                </div>
                <output class="form-control"><%= item.getPower() %> h.p.
                </output>
            </div>
        </div>
    </div>

    <div class="row">
        <div class="col">

            <form class="mt-3 add-form" action="<%= request.getContextPath() %>/update/<%= item.getId() %>" method="post"
                  enctype="multipart/form-data">
                <div class="form-row pr-0 pl-0">
                    <div class="col-sm-12 text-center">
                        <h2>Update auto</h2>
                    </div>
                </div>

                <div class="form-row mt-3 pr-0 pl-0">
                    <div class="col-sm-12 pr-0 pl-0">
                        <label for="name">Auto Name</label>
                        <input type="text" class="form-control" id="name" name="name" required placeholder="<%= item.getName() %>">
                    </div>
                </div>

                <div class="form-row mt-3">
                    <div class="col-sm-6 pr-0 pl-0">
                        <label for="color">Color</label>
                        <select class="form-control" name="color" id="color" required>
                            <% for (String color : (List<String>) request.getAttribute("colors")) { %>
                                <% if (color.equals(item.getColor())) {%>
                                    <option selected value="<%= color %>"><%= color %>
                                    </option>
                                <% } else { %>
                                    <option value="<%= color %>"><%= color %>
                                    </option>
                                <% } %>
                            <% } %>
                        </select>
                    </div>
                    <div class="col-sm-6 pr-0 pl-0">
                        <label for="power">Power</label>
                        <input name="power" class="form-control" id="power" required placeholder="<%= item.getPower() %>"/>
                    </div>
                </div>

                <div class="form-row mt-3">
                    <div class="col-sm-12 pr-0 pl-0">
                        <label for="year">Year</label>
                        <output name="yearOutput" id="yearOutput"><%=item.getYear()%></output>
                        <input type="range" name="year" id="year" class="custom-range" min="1980" max="2019" step="1"
                               oninput="yearOutput.value = year.value" value="<%=item.getYear()%>" required>
                        <small id="yearHelpInline" class="text-muted">
                            Range of values 1980..2019.
                        </small>
                    </div>
                </div>

                <div class="form-row mt-3">
                    <div class="col-sm-12 pr-0 pl-0">
                        <div class="custom-file">
                            <label class="custom-file-label" for="file">Choose file...</label>
                            <input type="file" class="custom-file-input" id="file" name="file" accept="image/*"
                                   required>
                        </div>
                    </div>
                </div>

                <div class="form-row mt-3">
                    <div class="col pr-0 pl-0">
                        <button type="submit" class="btn btn-block btn-info mt-2">Update</button>
                    </div>
                </div>
            </form>
        </div>
    </div>

    <div class="row">
        <div class="col">
            <form class="mt-3 add-form" action="<%= request.getContextPath() %>/csv/upload/" method="post"
                  enctype="multipart/form-data">

                <div class="form-row mt-5 pr-0 pl-0">
                    <div class="col-sm-12 text-center">
                        <h2>Upload from csv file</h2>
                    </div>
                </div>
                <div class="form-row mt-3">
                    <div class="col-sm-10 mt-2 pr-0 pl-0">
                        <div class="custom-file">
                            <label class="custom-file-label" for="file">Choose csv file...</label>
                            <input type="file" class="custom-file-input" id="csv-file" name="csv-file" accept="text/csv"
                                   required>
                        </div>
                    </div>
                    <div class="col-sm-2 pr-0 pl-0">
                        <button type="submit" class="btn btn-block btn-info mt-2">Upload</button>
                    </div>
                </div>
            </form>
        </div>
    </div>

</div>

<% } else { %>
<h3>No content</h3>
<% }%>


</body>
</html>
