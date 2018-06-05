package com.demo.application.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.demo.application.arcgis.util.ArcGISUtil;
import com.demo.application.geotab.Coordinates;

@WebServlet("/testArcGIS")
public class ArcGISTestServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public ArcGISTestServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.getWriter().append(ArcGISUtil.getRoadDistance(new Coordinates(12.936027, 77.691249), new Coordinates(12.956518, 77.701054)).toString());
	}

}
