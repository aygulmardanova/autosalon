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
    <link rel="stylesheet" href="../styles/styles.css" class="css">
</head>
<body>
<div class="container">
    <div class="row">
        <div class="col">
            <h1>Catalog</h1>

            <form action="<%= request.getContextPath() %>/catalog/">
                <div class="row mt-5">
                    <div class="col-sm-3 pr-0 pl-0">
                        <select class="form-control" name="searchType">
                            <option value="n">Name</option>
                            <option value="c">Color</option>
                            <option value="y">Year</option>
                            <option value="p">Power</option>
                        </select>
                    </div>
                    <div class="col-sm-7 pr-0 pl-0">
                        <input name="q" class="form-control" type="search" placeholder="Search">
                    </div>
                    <div class="col-sm-2 pr-0 pl-0">
                        <button class="btn btn-block btn-info">Search</button>
                    </div>
                </div>
            </form>

            <div class="row justify-content-end mt-3 mb-3">

                <div class="col-2 pl-0 pr-1">
                    <a href="<%= request.getContextPath() %>/create/"
                       class="btn btn-success btn-block">Create new</a>
                </div>
                <div class="col-2 pl-0 pr-0">
                    <a href="<%= request.getContextPath() %>/csv/download/"
                       class="btn btn-success btn-block">Download CSV</a>
                </div>
            </div>

            <% if (request.getAttribute("items") != null && !((List) request.getAttribute("items")).isEmpty()) { %>
            <% for (Auto item : (List<Auto>) request.getAttribute("items")) { %>
            <div class="row car-row mt-1">
                <div class="col-sm-5 pl-0">
                        <img src="<%= request.getContextPath() %>/images/<%= item.getImage() %>" class="rounded float-left card-img-top pr-0 pl-0 ml-0 mt-0" width="250px">
                </div>
                <div class="col-sm-4 mt-3 columns mt-5">
                        <div class="card-body">
                            <h3 class="card-title car-text"><%= item.getName() %>
                            </h3>
                        </div>
                </div>
                <div class="col-sm-3 mt-3 columns">
                    <div class="card-body">
                        <p class="card-text car-text"><%= item.getColor()%></p>
                        <p class="card-text car-text"><%= item.getYear()%> year</p>
                        <p class="card-text car-text"><%= item.getPower()%> h.p.</p>
                        <div class="row">
                            <div class="col-sm-6 pl-0 pr-0">
                                <a href="<%= request.getContextPath() %>/catalog/<%= item.getId() %>"
                                   class="btn btn-info btn-block">Details</a>
                            </div>
                            <div class="col-sm-6 pl-0 pr-0">
                                <a href="<%= request.getContextPath() %>/delete/<%= item.getId() %>"
                                   class="btn btn-danger btn-block">Delete</a>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <% } %>
            <% } else { %>
            <div class="row mt-4 justify-content-center">
                <h3><%= request.getAttribute("emptyMessage") %>
                </h3>
            </div>
            <% } %>

        </div>
    </div>
</div>

</body>
</html>
