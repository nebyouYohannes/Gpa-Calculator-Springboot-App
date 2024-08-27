package com.example.gpaCalculator.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;
import com.example.gpaCalculator.model.*;

@Controller
public class courseController {
		
	private static final String RDF_DATA_FILE = "./rdf/course.ttl"; // Replace with the path to your RDF data file
    private static final String RDF_DATA_NS = "http://example.org/amazon#";
    String exNS = "http://example.com/";
    
    @GetMapping("/")
    public String index(Model model, @RequestParam(name="search" , required=false) String search) {
    	if (search == null) {
            return "index"; 
        }
    	File file = new File(RDF_DATA_FILE);
         boolean empty = false;
        
            if (FileUtils.sizeOf(file) == 0) {
                empty = true;
            } 

    	
    	
    	
    	// Create a Jena model and load the RDF data from the file
        Dataset dataset = DatasetFactory.create(RDF_DATA_FILE);
        org.apache.jena.rdf.model.Model rdfModel = dataset.getDefaultModel();

        // Construct a SPARQL query to search for products based on the user's input
        String queryString = "PREFIX ex: <" + RDF_DATA_NS + ">\n" +
                            "SELECT ?Code ?Name ?creditHr ?grade WHERE {\n" +
                            "    ?course ex:hasCode ?Code .\n" +
                            "    ?course ex:hasName ?Name .\n" +
                            "    ?course ex:hasCreditHr ?creditHr .\n" +
                            "    ?course ex:hasGrade ?grade .\n" +
                            "    FILTER (regex(?Name, \"" + search + "\", \"i\"))\n" +
                            "}";

        Query qry = QueryFactory.create(queryString);
        QueryExecution qexec = QueryExecutionFactory.create(qry, rdfModel);

        ResultSet results = qexec.execSelect();

        List<Course> courseList = new ArrayList<>();
        while (results.hasNext()) {
            QuerySolution soln = results.nextSolution();
            Course course = new Course(
            		soln.getLiteral("Code").getString(),
                    soln.getLiteral("Name").getString(),
                    soln.getLiteral("creditHr").getInt(),
                    soln.getLiteral("grade").getString()
                );
            courseList.add(course);
        }

        model.addAttribute("courses", courseList);
        model.addAttribute("empt", empty);
        qexec.close();
    	
    	return "index";
    }
	
    
    @GetMapping("/addCourse")
    public String addCourse(){
    	return "addCourses";
    }
	
    
    @PostMapping("/save")
    public String saveProduct(Model model , @RequestParam(name="Code") String Code , @RequestParam(name="Name") String Name , @RequestParam(name="creditHr") int creditHr, @RequestParam(name="grade") String grade) throws IOException {
    	org.apache.jena.rdf.model.Model rdfModel = ModelFactory.createDefaultModel();
    	org.apache.jena.rdf.model.Model existingModel = ModelFactory.createDefaultModel();
    	existingModel.read(RDF_DATA_FILE, "TTL");
    	
    	String codeToFind=Code;
    	boolean added = false; 
    	
    	// Create a flag to check if the course exists
    	if (!existingModel.listSubjectsWithProperty(existingModel.createProperty("http://example.org/amazon#hasCode"), codeToFind)
    	        .toList().isEmpty()) {
    	    return "index";
    	    
    	} else {
    	    
    	
           //String strPrice=Float.toString(price);
           int productCounter = 0;
           try (BufferedReader reader = new BufferedReader(new FileReader("./rdf/counter.txt"))) {
               productCounter = Integer.parseInt(reader.readLine());
           } catch (IOException e) {
               e.printStackTrace();
           }

           
           Resource course = rdfModel.createResource(RDF_DATA_NS + "Course" + productCounter);
           Property hasCode = rdfModel.createProperty(RDF_DATA_NS, "hasCode");
           Property hasName = rdfModel.createProperty(RDF_DATA_NS, "hasName");
           Property hasCreditHr = rdfModel.createProperty(RDF_DATA_NS, "hasCreditHr");
           Property hasGrade = rdfModel.createProperty(RDF_DATA_NS, "hasGrade");

           course.addProperty(RDF.type, rdfModel.createResource(RDF_DATA_NS + "Course"))
                   .addProperty(hasCode, Code)
                   .addProperty(hasName, Name)
                   .addProperty(hasCreditHr, rdfModel.createTypedLiteral(String.valueOf(creditHr), XSDDatatype.XSDint))
                   .addProperty(hasGrade, grade);

    
           productCounter++;
           // Convert the RDF model to Turtle format
           StringWriter out = new StringWriter();
           rdfModel.write(out, "TURTLE");
           
           // Append the RDF data to the Product.ttl file
           File file = new File(RDF_DATA_FILE);
           FileWriter fr = new FileWriter(file, true); // true parameter for append mode
           BufferedWriter br = new BufferedWriter(fr);
           br.write(out.toString()); // Write the RDF data to the file
           br.close(); // Close the writer
           
           try (BufferedWriter writer = new BufferedWriter(new FileWriter("./rdf/counter.txt"))) {
        	    writer.write(String.valueOf(productCounter));
        	    added = true;
        	} catch (IOException e) {
        	    e.printStackTrace();
        	}
    	
    	 model.addAttribute("added2", added);
    	return "addCourses";
    	}
    } 
    
    
    @PostMapping("/delete")
    public String deleteObject(Model model ,@RequestParam("title") String title) throws IOException {
    	org.apache.jena.rdf.model.Model rdfModel = ModelFactory.createDefaultModel();
    	rdfModel.read(RDF_DATA_FILE, "TTL");
    	String codeToFind = title;
       boolean deleted=false;
    	

        Resource prod = rdfModel.listSubjectsWithProperty(rdfModel.createProperty("http://example.org/amazon#hasCode"), codeToFind)
                .toList().get(0);
        String prodURI = prod.getURI();
    	 
    	 Resource resourceToDelete = rdfModel.getResource(prodURI);
    	 rdfModel.removeAll(resourceToDelete, null, (RDFNode) null);
    	 rdfModel.removeAll(null, null, resourceToDelete);
    	 try (OutputStream outputStream = new FileOutputStream(RDF_DATA_FILE)) {
    		    rdfModel.write(outputStream, "TTL");
    		    deleted=true;
    		}
    	 
    	model.addAttribute("dele", deleted);
     
        return "index";
    }
    
    @PostMapping("/calculateGpa")
    public String calculate (Model model) {
    	File file = new File(RDF_DATA_FILE);
        boolean empty = false;
       
           if (FileUtils.sizeOf(file) == 0) {
               empty = true;
           }
    	org.apache.jena.rdf.model.Model rdfModel = ModelFactory.createDefaultModel();
    	rdfModel.read(RDF_DATA_FILE, "TTL");
    	
    	 String queryString = "PREFIX ex: <" + RDF_DATA_NS + ">\n" +
                 "SELECT ?Code ?Name ?creditHr ?grade WHERE {\n" +
                 "    ?course ex:hasCode ?Code .\n" +
                 "    ?course ex:hasName ?Name .\n" +
                 "    ?course ex:hasCreditHr ?creditHr .\n" +
                 "    ?course ex:hasGrade ?grade .\n" +                 
                 "}";

    	 Query qry = QueryFactory.create(queryString);
    	 QueryExecution qexec = QueryExecutionFactory.create(qry, rdfModel);

    	 ResultSet results = qexec.execSelect();

    	 List<Course> courseList = new ArrayList<>();
    	 while (results.hasNext()) {
    		 QuerySolution soln = results.nextSolution();
    		 Course course = new Course(
    				 soln.getLiteral("Code").getString(),
    				 soln.getLiteral("Name").getString(),
    				 soln.getLiteral("creditHr").getInt(),
    				 soln.getLiteral("grade").getString()
    				 );
    		 courseList.add(course);
    	 }

    	 model.addAttribute("courses", courseList);
    	 qexec.close();
    	 
    	 double totalCredits = 0;
         double totalGradePoints = 0;
         double gradeNo=0;
         int totalNoOfCourses = 0;
         
         for (Course c : courseList) {
             totalCredits += c.getCreditHr();
             totalNoOfCourses += 1;
             if (c.getGrade().equals("A") || c.getGrade().equals("A+")) {
            	    gradeNo = 4;
            	} else if (c.getGrade().equals("B+") || c.getGrade().equals("b+")) {
            	    gradeNo = 3.5;
            	} else if (c.getGrade().equals("B") || c.getGrade().equals("b")) {
            	    gradeNo = 3;
            	} else if (c.getGrade().equals("C+") || c.getGrade().equals("c+")) {
            	    gradeNo = 2.5;
            	} else if (c.getGrade().equals("C") || c.getGrade().equals("c")) {
            	    gradeNo = 2;
            	} else if (c.getGrade().equals("D") || c.getGrade().equals("d")) {
            	    gradeNo = 1;
            	} else if (c.getGrade().equals("F") || c.getGrade().equals("f")) {
            	    gradeNo = 0;
            	}
             
             totalGradePoints += gradeNo * c.getCreditHr();
         }
         
         double calculatedGPA = totalGradePoints / totalCredits;
         
         DecimalFormat decimalFormat = new DecimalFormat("#.00");
         String formattedNumber = decimalFormat.format(calculatedGPA);
         
         // Add the calculated CGPA to the model
         model.addAttribute("cgpa", formattedNumber);
         model.addAttribute("totCredit", totalCredits);
         model.addAttribute("totCourses", totalNoOfCourses);
         model.addAttribute("empt", empty);
         
    	
    	return "index";
    }
}
