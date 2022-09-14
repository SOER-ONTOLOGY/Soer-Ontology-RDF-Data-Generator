import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.RDFWriter;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;

/**
 * @Title CSVGeneratorRDF
 * @Description CSVGen to create rdf from a CSV file
 * @author Marco Augusto Caicedo Burneo
 */

public class RdfGeneratorFinal {
    //CAMBIAR POR RUTAS DEL EQUIPO DONDE SE EJECUTE
    static String FolderPath="C:/Users/Ludiott/Documents/Tesis_Logs/RDF_DATA/";
    static String coursesData ="Curso.csv";
    static String personsData ="Equipo.csv";
    static String sectionsData ="Section.csv";
    static String subsectionsData ="Subsection.csv";
    static String unitsData ="Unit.csv";
    static String logsData ="Logs.csv";
    static String eventsData ="Eventos.csv";
    static String metadataData ="Metadata.csv";
    static String chaptersData ="Chapter.csv";
    static String GenFilePath= "C:/Users/Ludiott/Documents/Tesis_Logs/RDF_DATA/Soer-MCS31Nuevo.rdf"; //Generated RDF

    static String uriCourse ="https://utpl.edu.ec/soer/data/courses/";
    static String uriEquip="https://utpl.edu.ec/soer/data/equip/";
    static String uriLog="https://utpl.edu.ec/soer/data/logs/";

    static String uriSection="https://utpl.edu.ec/soer/data/courses/sections/";
    static String uriSubsection="https://utpl.edu.ec/soer/data/courses/subsections/";
    static String uriUnit="https://utpl.edu.ec/soer/data/courses/units/";
    static String uriEvent="https://utpl.edu.ec/soer/data/courses/events/";
    static String uriMetadata="https://utpl.edu.ec/soer/data/courses/events/metadata/";
    static String uriChapter="https://utpl.edu.ec/soer/data/courses/events/chapter/";

    public static void main(String... args) throws FileNotFoundException {
        //Get data from CSV and store in a list
        List<Courses> courses = readCoursesFromCSV(FolderPath+coursesData);
        List<Persons> persons = readPersonsFromCSV(FolderPath+personsData);
        List<Logs> logs = readLogsFromCSV(FolderPath+logsData);
        List<Sections> sections = readSectionsFromCSV(FolderPath+sectionsData);
        List<Subsections> subsections = readSubsectionsFromCSV(FolderPath+subsectionsData);
        List<Units> units = readUnitsFromCSV(FolderPath+unitsData);
        List<Events> events = readEventsFromCSV(FolderPath+eventsData);
        List<Metadata> metadata = readMetadataFromCSV(FolderPath+metadataData);
        List<Chapters> chapters = readChaptersFromCSV(FolderPath+chaptersData);
        
        // create an empty Model
        Model model = ModelFactory.createDefaultModel();

        File f= new File (GenFilePath); //File to save the results of RDF Generation
        FileOutputStream os = new FileOutputStream(f);

        //Set prefix for the URI base (data)
        String dataPrefix = "https://utpl.edu.ec/soer/data/";
        model.setNsPrefix("soerData",dataPrefix);


        String ontoPrefix="https://utpl.edu.ec/soer/ontology#";
        Model ontoModel = ModelFactory.createDefaultModel();
        ontoModel.setNsPrefix("soerOnto",ontoPrefix);

        //Vocab and models present in JENA
        
        //Schema
        String schema = "http://schema.org/";
        model.setNsPrefix("schema", schema);
        Model schemaModel = ModelFactory.createDefaultModel();
        
        //Teach
        String teach = "http://linkedscience.org/teach/ns/";
        model.setNsPrefix("teach", teach);
        Model teachModel = ModelFactory.createDefaultModel();
        
        //DCMI Metadata Terms
        String dcterms = "https://www.dublincore.org/specifications/dublin-core/dcmi-terms/";
        model.setNsPrefix("dcterms", dcterms);
        Model dctermsModel = ModelFactory.createDefaultModel();
        
        //vcard
        String vcard = "http://www.w3.org/2006/vcard/ns#";
        model.setNsPrefix("vcard", vcard);
        Model vcardModel = ModelFactory.createDefaultModel();

        //ids
        String ids = "https://w3id.org/idsa/core/";
        model.setNsPrefix("ids", ids);
        Model idsModel = ModelFactory.createDefaultModel();
        
        //sioc
        String sioc = "http://rdfs.org/sioc/ns#";
        model.setNsPrefix("sioc", sioc);
        Model siocModel = ModelFactory.createDefaultModel();
        
        //sio
        String sio = "https://semanticscience.org/resource/";
        model.setNsPrefix("sio", sio);
        Model sioModel = ModelFactory.createDefaultModel();
        
        //xsd
        String xs = "http://www.w3.org/2001/XMLSchema#";
        model.setNsPrefix("xs", xs);
        Model xsModel = ModelFactory.createDefaultModel();

        for(Courses a : courses ){
            System.out.println(a);
            Resource rOc = model.createResource(uriCourse+a.getCode())                    
                    .addProperty(RDF.type,teachModel.getProperty(teach,"Course"))
                    .addProperty(ontoModel.getProperty(ontoPrefix,"code"), a.getCode())
                    .addProperty(RDFS.label, a.getLabel())
                    .addProperty(schemaModel.getProperty(schema,"startDate"), a.getStartDate())
                    .addProperty(schemaModel.getProperty(schema,"endDate"), a.getEndDate());
        }
        
        for(Persons a : persons ){
            System.out.println(a);
            Resource rOc = model.createResource(uriEquip+a.getUser_name())                                        
                    .addProperty(RDF.type,FOAF.Person)
                    .addProperty(idsModel.getProperty(ids,"authUserName"), a.getUser_name())
                    .addProperty(ontoModel.getProperty(ontoPrefix,"role"), a.getRole())
                    .addProperty(teachModel.getProperty(teach,"teacherOf"),  model.getProperty(uriCourse+a.getCourse_code()));
        }
        
        for(Logs a : logs ){
            System.out.println(a);
            Resource rOc = model.createResource(uriLog+a.getName())                    
                    .addProperty(RDF.type,ontoModel.getProperty(ontoPrefix,"Log"))
                    .addProperty(RDFS.label, a.getName())
                    .addProperty(xsModel.getProperty(xs,"date"), a.getDate())
                    .addProperty(xsModel.getProperty(xs,"time"), a.getTime());
        }
        
        for(Sections a : sections ){
            System.out.println(a);
            Resource rOc = model.createResource(uriSection+a.getId())                    
                    .addProperty(RDF.type,ontoModel.getProperty(ontoPrefix,"Sections"))
                    .addProperty(siocModel.getProperty(sioc,"id"), a.getId())
                    .addProperty(RDFS.label, a.getDisplay_name())
                    .addProperty(schemaModel.getProperty(schema,"startDate"), a.getStartDate())
                    .addProperty(xsModel.getProperty(xs,"time"), a.getTime())
                    .addProperty(ontoModel.getProperty(ontoPrefix,"visible_to_student"), String.valueOf(a.getVisible_to_staff_only()))
                    .addProperty(ontoModel.getProperty(ontoPrefix,"request_type"), a.getType())
                    .addProperty(ontoModel.getProperty(ontoPrefix,"belongsTo"), model.getProperty(uriCourse+a.getCourse_code()));
        }
        
        for(Subsections a : subsections ){
            System.out.println(a);
            Resource rOc = model.createResource(uriSubsection+a.getId())                    
                    .addProperty(RDF.type,ontoModel.getProperty(ontoPrefix,"Subsections"))
                    .addProperty(ontoModel.getProperty(ontoPrefix,"has"), model.getProperty(uriSection+a.getSection_id()))
                    .addProperty(siocModel.getProperty(sioc,"id"), a.getId())
                    .addProperty(RDFS.label, a.getDisplay_name())
                    .addProperty(schemaModel.getProperty(schema,"startDate"), a.getStartDate())
                    .addProperty(xsModel.getProperty(xs,"time"), a.getTime())
                    .addProperty(ontoModel.getProperty(ontoPrefix,"visible_to_student"), String.valueOf(a.getVisible_to_staff_only()))
                    .addProperty(ontoModel.getProperty(ontoPrefix,"grader_type"), a.getGrader_type())
                    .addProperty(ontoModel.getProperty(ontoPrefix,"request_type"), a.getType())
                    .addProperty(ontoModel.getProperty(ontoPrefix,"belongsTo"), model.getProperty(uriCourse+a.getCourse_code()));
        }
        
        for(Units a : units ){
            System.out.println(a);
            Resource rOc = model.createResource(uriUnit+a.getId())                    
                    .addProperty(RDF.type,ontoModel.getProperty(ontoPrefix,"Units"))
                    .addProperty(ontoModel.getProperty(ontoPrefix,"has"), model.getProperty(uriSubsection+a.getSubsection_id()))
                    .addProperty(siocModel.getProperty(sioc,"id"), a.getId())
                    .addProperty(RDFS.label, a.getDisplay_name())
                    .addProperty(ontoModel.getProperty(ontoPrefix,"request_type"), a.getType());
        }
        
        for(Events a : events ){      
            if ( (a.getType()).equals("Content_Update") &&  (a.getPush_notification_selected()).equals("vacio")) {
                Resource rOc = model.createResource(uriEvent+a.getId())                    
                    .addProperty(RDF.type,ontoModel.getProperty(ontoPrefix,"Events"))                        
                    .addProperty(dctermsModel.getProperty(dcterms,"ispartOf"), model.getProperty(uriLog+a.getLog_name()))
                    .addProperty(ontoModel.getProperty(ontoPrefix,"type"), a.getType())
                    .addProperty(ontoModel.getProperty(ontoPrefix,"generatedBy"), model.getProperty(uriEquip+a.getUsername()))
                    .addProperty(xsModel.getProperty(xs,"time"), a.getTime())
                    .addProperty(ontoModel.getProperty(ontoPrefix,"events_type"), a.getEventsType())
                    .addProperty(ontoModel.getProperty(ontoPrefix,"html"), a.getHtml())
                    .addProperty(ontoModel.getProperty(ontoPrefix,"event_number"), String.valueOf(a.getId()));
            }
            
            if ( (a.getType()).equals("Content_Update") &&  !(a.getPush_notification_selected()).equals("vacio") &&  !(a.getHtml()).equals("vacio")) {
                Resource rOc = model.createResource(uriEvent+a.getId())                    
                    .addProperty(RDF.type,ontoModel.getProperty(ontoPrefix,"Events"))
                    .addProperty(dctermsModel.getProperty(dcterms,"ispartOf"), model.getProperty(uriLog+a.getLog_name()))
                    .addProperty(ontoModel.getProperty(ontoPrefix,"type"), a.getType())
                    .addProperty(ontoModel.getProperty(ontoPrefix,"generatedBy"), model.getProperty(uriEquip+a.getUsername()))
                    .addProperty(xsModel.getProperty(xs,"time"), a.getTime())
                    .addProperty(ontoModel.getProperty(ontoPrefix,"events_type"), a.getEventsType())
                    .addProperty(ontoModel.getProperty(ontoPrefix,"push_notification_selected"), a.getPush_notification_selected())
                    .addProperty(ontoModel.getProperty(ontoPrefix,"push_notification_enabled"), a.getPush_notification_enabled())
                    .addProperty(ontoModel.getProperty(ontoPrefix,"html"), a.getHtml())
                    .addProperty(ontoModel.getProperty(ontoPrefix,"event_number"), String.valueOf(a.getId()));
            }
            
            if ((a.getType()).equals("Content_Update") &&  (a.getHtml()).equals("vacio") &&  (a.getId2()).equals("vacio") &&  (a.getGrader_type()).equals("vacio")) {
                Resource rOc = model.createResource(uriEvent+a.getId())                    
                    .addProperty(RDF.type,ontoModel.getProperty(ontoPrefix,"Events"))
                    .addProperty(dctermsModel.getProperty(dcterms,"ispartOf"), model.getProperty(uriLog+a.getLog_name()))
                    .addProperty(ontoModel.getProperty(ontoPrefix,"type"), a.getType())
                    .addProperty(ontoModel.getProperty(ontoPrefix,"generatedBy"), model.getProperty(uriEquip+a.getUsername()))
                    .addProperty(xsModel.getProperty(xs,"time"), a.getTime())
                    .addProperty(ontoModel.getProperty(ontoPrefix,"events_type"), a.getEventsType())
                    .addProperty(ontoModel.getProperty(ontoPrefix,"push_notification_selected"), a.getPush_notification_selected())
                    .addProperty(ontoModel.getProperty(ontoPrefix,"push_notification_enabled"), a.getPush_notification_enabled())
                    .addProperty(ontoModel.getProperty(ontoPrefix,"event_number"), String.valueOf(a.getId()));
            }
            
            if ((a.getType()).equals("Content_Update") &&  (a.getHtml()).equals("vacio") &&  (a.getId2()).equals("vacio") &&  !(a.getGrader_type()).equals("vacio")) {
                Resource rOc = model.createResource(uriEvent+a.getId())                    
                    .addProperty(RDF.type,ontoModel.getProperty(ontoPrefix,"Events"))
                    .addProperty(dctermsModel.getProperty(dcterms,"ispartOf"), model.getProperty(uriLog+a.getLog_name()))
                    .addProperty(ontoModel.getProperty(ontoPrefix,"type"), a.getType())
                    .addProperty(ontoModel.getProperty(ontoPrefix,"generatedBy"), model.getProperty(uriEquip+a.getUsername()))
                    .addProperty(xsModel.getProperty(xs,"time"), a.getTime())
                    .addProperty(ontoModel.getProperty(ontoPrefix,"events_type"), a.getEventsType())
                    .addProperty(ontoModel.getProperty(ontoPrefix,"grader_type"), a.getGrader_type())
                    .addProperty(ontoModel.getProperty(ontoPrefix,"event_number"), String.valueOf(a.getId()));
            }
            
            if ((a.getType()).equals("Content_Update") &&  (a.getHtml()).equals("vacio") &&  !(a.getId2()).equals("vacio")) {
                Resource rOc = model.createResource(uriEvent+a.getId())                    
                    .addProperty(RDF.type,ontoModel.getProperty(ontoPrefix,"Events"))
                    .addProperty(dctermsModel.getProperty(dcterms,"ispartOf"), model.getProperty(uriLog+a.getLog_name()))
                    .addProperty(ontoModel.getProperty(ontoPrefix,"type"), a.getType())
                    .addProperty(ontoModel.getProperty(ontoPrefix,"generatedBy"), model.getProperty(uriEquip+a.getUsername()))
                    .addProperty(xsModel.getProperty(xs,"time"), a.getTime())
                    .addProperty(ontoModel.getProperty(ontoPrefix,"events_type"), a.getEventsType())                        
                    .addProperty(ontoModel.getProperty(ontoPrefix,"id"), a.getId2())
                    .addProperty(ontoModel.getProperty(ontoPrefix,"push_notification_selected"), a.getPush_notification_selected())
                    .addProperty(ontoModel.getProperty(ontoPrefix,"push_notification_enabled"), a.getPush_notification_enabled())
                    .addProperty(ontoModel.getProperty(ontoPrefix,"event_number"), String.valueOf(a.getId()));
            }
            
            if ((a.getType()).equals("Settings_Team")) {
                Resource rOc = model.createResource(uriEvent+a.getId())                    
                    .addProperty(RDF.type,ontoModel.getProperty(ontoPrefix,"Events"))
                    .addProperty(dctermsModel.getProperty(dcterms,"ispartOf"), model.getProperty(uriLog+a.getLog_name()))
                    .addProperty(ontoModel.getProperty(ontoPrefix,"type"), a.getType())
                    .addProperty(ontoModel.getProperty(ontoPrefix,"generatedBy"), model.getProperty(uriEquip+a.getUsername()))
                    .addProperty(xsModel.getProperty(xs,"time"), a.getTime())
                    .addProperty(ontoModel.getProperty(ontoPrefix,"events_type"), a.getEventsType())                        
                    .addProperty(ontoModel.getProperty(ontoPrefix,"role"), a.getRole())
                    .addProperty(ontoModel.getProperty(ontoPrefix,"event_number"), String.valueOf(a.getId()));

            }
            
            if ((a.getType()).equals("Settings_Details") &&  (a.getHtml()).equals("vacio")) {
               Resource rOc = model.createResource(uriEvent+a.getId())                    
                    .addProperty(RDF.type,ontoModel.getProperty(ontoPrefix,"Events"))
                    .addProperty(dctermsModel.getProperty(dcterms,"ispartOf"), model.getProperty(uriLog+a.getLog_name()))
                    .addProperty(ontoModel.getProperty(ontoPrefix,"type"), a.getType())
                    .addProperty(ontoModel.getProperty(ontoPrefix,"generatedBy"), model.getProperty(uriEquip+a.getUsername()))
                    .addProperty(xsModel.getProperty(xs,"time"), a.getTime())
                    .addProperty(ontoModel.getProperty(ontoPrefix,"events_type"), a.getEventsType())
                    .addProperty(ontoModel.getProperty(ontoPrefix,"org"), a.getOrg())
                    .addProperty(ontoModel.getProperty(ontoPrefix,"course_id"), a.getCourse_id())
                    .addProperty(ontoModel.getProperty(ontoPrefix,"run"), a.getRun())
                    .addProperty(ontoModel.getProperty(ontoPrefix,"language"), a.getLanguage())
                    .addProperty(schemaModel.getProperty(schema,"startDate"), a.getStartDate())
                    .addProperty(schemaModel.getProperty(schema,"endDate"), a.getEndDate())
                    .addProperty(ontoModel.getProperty(ontoPrefix,"enrollment_start"), a.getEnrollment_start())
                    .addProperty(ontoModel.getProperty(ontoPrefix,"enrollment_end"), a.getEnrollment_end())
                    .addProperty(ontoModel.getProperty(ontoPrefix,"syllabus"), a.getSyllabus())
                    .addProperty(ontoModel.getProperty(ontoPrefix,"overview"), a.getOverview())
                    .addProperty(ontoModel.getProperty(ontoPrefix,"event_number"), String.valueOf(a.getId()));

            }
            
            if ((a.getType()).equals("Settings_Details") &&  !(a.getHtml()).equals("vacio")) {
               Resource rOc = model.createResource(uriEvent+a.getId())                    
                    .addProperty(RDF.type,ontoModel.getProperty(ontoPrefix,"Events"))
                    .addProperty(dctermsModel.getProperty(dcterms,"ispartOf"), model.getProperty(uriLog+a.getLog_name()))
                    .addProperty(ontoModel.getProperty(ontoPrefix,"type"), a.getType())
                    .addProperty(ontoModel.getProperty(ontoPrefix,"generatedBy"), model.getProperty(uriEquip+a.getUsername()))
                    .addProperty(xsModel.getProperty(xs,"time"), a.getTime())
                    .addProperty(ontoModel.getProperty(ontoPrefix,"events_type"), a.getEventsType())
                    .addProperty(ontoModel.getProperty(ontoPrefix,"html"), a.getHtml())
                    .addProperty(ontoModel.getProperty(ontoPrefix,"event_number"), String.valueOf(a.getId()));
            }
            
            if ((a.getType()).equals("Content_Books")) {
               Resource rOc = model.createResource(uriEvent+a.getId())                    
                    .addProperty(RDF.type,ontoModel.getProperty(ontoPrefix,"Events"))
                    .addProperty(dctermsModel.getProperty(dcterms,"ispartOf"), model.getProperty(uriLog+a.getLog_name()))
                    .addProperty(ontoModel.getProperty(ontoPrefix,"type"), a.getType())
                    .addProperty(ontoModel.getProperty(ontoPrefix,"generatedBy"), model.getProperty(uriEquip+a.getUsername()))
                    .addProperty(xsModel.getProperty(xs,"time"), a.getTime())
                    .addProperty(ontoModel.getProperty(ontoPrefix,"events_type"), a.getEventsType())
                    .addProperty(ontoModel.getProperty(ontoPrefix,"tab_title"), a.getTab_title())
                    .addProperty(ontoModel.getProperty(ontoPrefix,"event_number"), String.valueOf(a.getId()));
            }
            
            if (!(a.getCategory()).equals("vacio")) {
               Resource rOc = model.createResource(uriEvent+a.getId())                    
                    .addProperty(RDF.type,ontoModel.getProperty(ontoPrefix,"Events"))
                    .addProperty(dctermsModel.getProperty(dcterms,"ispartOf"), model.getProperty(uriLog+a.getLog_name()))
                    .addProperty(ontoModel.getProperty(ontoPrefix,"type"), a.getType())
                    .addProperty(ontoModel.getProperty(ontoPrefix,"generatedBy"), model.getProperty(uriEquip+a.getUsername()))
                    .addProperty(xsModel.getProperty(xs,"time"), a.getTime())
                    .addProperty(ontoModel.getProperty(ontoPrefix,"events_type"), a.getEventsType())
                    .addProperty(RDFS.label, a.getDisplay_name())
                    .addProperty(ontoModel.getProperty(ontoPrefix,"category"), a.getCategory())
                    .addProperty(ontoModel.getProperty(ontoPrefix,"parent_locator"), a.getParent_locator())
                    .addProperty(ontoModel.getProperty(ontoPrefix,"event_number"), String.valueOf(a.getId()));
            }
            
            if (!(a.getBloque()).equals("vacio") && !(a.getDisplay_name()).equals("vacio") && (a.getType()).equals("Chapter")) {
               Resource rOc = model.createResource(uriEvent+a.getId())                    
                    .addProperty(RDF.type,ontoModel.getProperty(ontoPrefix,"Events"))
                    .addProperty(dctermsModel.getProperty(dcterms,"ispartOf"), model.getProperty(uriLog+a.getLog_name()))
                    .addProperty(ontoModel.getProperty(ontoPrefix,"type"), a.getType())
                    .addProperty(ontoModel.getProperty(ontoPrefix,"has"), model.getProperty(uriSection+a.getBlock_id()))
                    .addProperty(ontoModel.getProperty(ontoPrefix,"generatedBy"), model.getProperty(uriEquip+a.getUsername()))
                    .addProperty(xsModel.getProperty(xs,"time"), a.getTime())
                    .addProperty(ontoModel.getProperty(ontoPrefix,"events_type"), a.getEventsType())
                    .addProperty(RDFS.label, a.getDisplay_name())
                    .addProperty(ontoModel.getProperty(ontoPrefix,"event_number"), String.valueOf(a.getId()));
            }
            
            if (!(a.getBloque()).equals("vacio") && !(a.getDisplay_name()).equals("vacio") && (a.getType()).equals("Sequential")) {
               Resource rOc = model.createResource(uriEvent+a.getId())                    
                    .addProperty(RDF.type,ontoModel.getProperty(ontoPrefix,"Events"))
                    .addProperty(dctermsModel.getProperty(dcterms,"ispartOf"), model.getProperty(uriLog+a.getLog_name()))
                    .addProperty(ontoModel.getProperty(ontoPrefix,"type"), a.getType())
                    .addProperty(ontoModel.getProperty(ontoPrefix,"has"), model.getProperty(uriSubsection+a.getBlock_id()))
                    .addProperty(ontoModel.getProperty(ontoPrefix,"generatedBy"), model.getProperty(uriEquip+a.getUsername()))
                    .addProperty(xsModel.getProperty(xs,"time"), a.getTime())
                    .addProperty(ontoModel.getProperty(ontoPrefix,"events_type"), a.getEventsType())
                    .addProperty(RDFS.label, a.getDisplay_name())
                    .addProperty(ontoModel.getProperty(ontoPrefix,"event_number"), String.valueOf(a.getId()));
            }
            
            if (!(a.getBloque()).equals("vacio") && !(a.getPublish()).equals("vacio") && (a.getType()).equals("Sequential")) {
               Resource rOc = model.createResource(uriEvent+a.getId())                    
                    .addProperty(RDF.type,ontoModel.getProperty(ontoPrefix,"Events"))
                    .addProperty(dctermsModel.getProperty(dcterms,"ispartOf"), model.getProperty(uriLog+a.getLog_name()))
                    .addProperty(ontoModel.getProperty(ontoPrefix,"type"), a.getType())
                    .addProperty(ontoModel.getProperty(ontoPrefix,"has"), model.getProperty(uriSubsection+a.getBlock_id()))
                    .addProperty(ontoModel.getProperty(ontoPrefix,"generatedBy"), model.getProperty(uriEquip+a.getUsername()))
                    .addProperty(xsModel.getProperty(xs,"time"), a.getTime())
                    .addProperty(ontoModel.getProperty(ontoPrefix,"events_type"), a.getEventsType())
                    .addProperty(ontoModel.getProperty(ontoPrefix,"publish"), a.getPublish())                       
                    .addProperty(ontoModel.getProperty(ontoPrefix,"grader_type"), a.getGrader_type())
                    .addProperty(ontoModel.getProperty(ontoPrefix,"event_number"), String.valueOf(a.getId()));
            }
            
            if (!(a.getBloque()).equals("vacio") && !(a.getGrader_type()).equals("vacio") && (a.getPublish()).equals("vacio") && (a.getType()).equals("Sequential")) {
               Resource rOc = model.createResource(uriEvent+a.getId())                    
                    .addProperty(RDF.type,ontoModel.getProperty(ontoPrefix,"Events"))
                    .addProperty(dctermsModel.getProperty(dcterms,"ispartOf"), model.getProperty(uriLog+a.getLog_name()))
                    .addProperty(ontoModel.getProperty(ontoPrefix,"type"), a.getType())
                    .addProperty(ontoModel.getProperty(ontoPrefix,"has"), model.getProperty(uriSubsection+a.getBlock_id()))
                    .addProperty(ontoModel.getProperty(ontoPrefix,"generatedBy"), model.getProperty(uriEquip+a.getUsername()))
                    .addProperty(xsModel.getProperty(xs,"time"), a.getTime())
                    .addProperty(ontoModel.getProperty(ontoPrefix,"events_type"), a.getEventsType())                  
                    .addProperty(ontoModel.getProperty(ontoPrefix,"grader_type"), a.getGrader_type())
                    .addProperty(ontoModel.getProperty(ontoPrefix,"event_number"), String.valueOf(a.getId()));
            }
        }
        
        for(Metadata a : metadata ){
            System.out.println(a);
            if (!(a.getDisplay_name()).equals("vacio")) {
                Resource rOc = model.createResource(uriMetadata+a.getEvent_id())                    
                    .addProperty(RDF.type,sioModel.getProperty(sio,"SIO_001330"))                        
                    .addProperty(ontoModel.getProperty(ontoPrefix,"contains"), model.getProperty(uriEvent+String.valueOf(a.getEvent_id())))
                    .addProperty(RDFS.label, a.getDisplay_name());
            }
            
            if (!(a.getStart()).equals("vacio") && !(a.getDue()).equals("vacio")) {
                Resource rOc = model.createResource(uriMetadata+a.getEvent_id())                    
                    .addProperty(RDF.type,sioModel.getProperty(sio,"SIO_001330"))                        
                    .addProperty(ontoModel.getProperty(ontoPrefix,"contains"), model.getProperty(uriEvent+String.valueOf(a.getEvent_id())))
                    .addProperty(schemaModel.getProperty(schema,"startDate"), a.getStart())
                    .addProperty(xsModel.getProperty(xs,"date"), a.getDue());
            }
            
            if (!(a.getStart()).equals("vacio") && (a.getDue()).equals("vacio")) {
                Resource rOc = model.createResource(uriMetadata+a.getEvent_id())                     
                    .addProperty(RDF.type,sioModel.getProperty(sio,"SIO_001330"))                       
                    .addProperty(ontoModel.getProperty(ontoPrefix,"contains"), model.getProperty(uriEvent+String.valueOf(a.getEvent_id())))
                    .addProperty(schemaModel.getProperty(schema,"startDate"), a.getStart());
            }
            
            if ((a.getStart()).equals("vacio") && !(a.getDue()).equals("vacio")) {
                Resource rOc = model.createResource(uriMetadata+a.getEvent_id())                    
                    .addProperty(RDF.type,sioModel.getProperty(sio,"SIO_001330"))                        
                    .addProperty(ontoModel.getProperty(ontoPrefix,"contains"), model.getProperty(uriEvent+String.valueOf(a.getEvent_id())))
                    .addProperty(xsModel.getProperty(xs,"date"), a.getDue());
            }
            
            if (!(a.getVisible_to_staff_only()).equals("vacio")) {
                Resource rOc = model.createResource(uriMetadata+a.getId())                    
                    .addProperty(RDF.type,sioModel.getProperty(sio,"SIO_001330"))                        
                    .addProperty(ontoModel.getProperty(ontoPrefix,"contains"), model.getProperty(uriEvent+String.valueOf(a.getEvent_id())))
                    .addProperty(ontoModel.getProperty(ontoPrefix,"visible_to_student"), a.getVisible_to_staff_only()) ;
            }
        }
        
        for(Chapters a : chapters ){
            System.out.println(a);
            Resource rOc = model.createResource(uriChapter+a.getId())                    
                    .addProperty(RDF.type,ontoModel.getProperty(ontoPrefix,"Chapter"))                        
                    .addProperty(ontoModel.getProperty(ontoPrefix,"contains"), model.getProperty(uriEvent+String.valueOf(a.getEvent_id())))
                    .addProperty(dctermsModel.getProperty(dcterms,"title"), a.getTitle())
                    .addProperty(schemaModel.getProperty(schema,"url"), a.getUrl());
        }
        
        /**
         * Reading the Generated data in Triples Format and RDF
         */
        StmtIterator iter = model.listStatements();
        System.out.println("TRIPLES");
        while (iter.hasNext()) {
            Statement stmt      = iter.nextStatement();  // get next statement
            Resource  subject   = stmt.getSubject();     // get the subject
            Property  predicate = stmt.getPredicate();   // get the predicate
            RDFNode   object    = stmt.getObject();      // get the object

            System.out.print(subject.toString());
            System.out.print(" " + predicate.toString() + " ");
            if (object instanceof Resource) {
                System.out.print(object.toString());
            } else {
                // object is a literal
                System.out.print(" \"" + object.toString() + "\"");
            }

            System.out.println(" .");
        }
        // now write the model in XML form to a file
        System.out.println("MODELO RDF------");
        model.write(System.out, "RDF/XML-ABBREV");

        // Save to a file
        RDFWriter writer = model.getWriter("RDF/XML");
        writer.write(model,os, "");

        //Close models
        model.close();
    }

    private static List<Courses> readCoursesFromCSV(String fileName) {
        List<Courses> courses = new ArrayList<>();
        Path pathToFile = Paths.get(fileName);

        // create an instance of BufferedReader
        // using try with resource, Java 7 feature to close resources
        try ( BufferedReader br = Files.newBufferedReader(pathToFile)) {
            // read the first line from the text file
            String line = br.readLine();
            // loop until all lines are read
            while (line != null) {
                // use string.split to load a string array with the values from
                // each line of
                // the file, using a comma as the delimiter
                String[] attributes = line.split(",");
                //System.out.println(attributes.length);
                Courses course = createCourses(attributes);

                // adding person into ArrayList
                courses.add(course);

                // read next line before looping
                // if end of file reached, line would be null
                line = br.readLine();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return courses;
    }
    private static List<Persons> readPersonsFromCSV(String fileName) {
        List<Persons> persons = new ArrayList<>();
        Path pathToFile = Paths.get(fileName);
        // create an instance of BufferedReader
        // using try with resource, Java 7 feature to close resources
        try ( BufferedReader br = Files.newBufferedReader(pathToFile)) {
            // read the first line from the text file
            String line = br.readLine();
            // loop until all lines are read
            while (line != null) {
                // use string.split to load a string array with the values from
                // each line of
                // the file, using a comma as the delimiter
                String[] attributes = line.split(",");
                //System.out.println(attributes.length);
                Persons person = createPersons(attributes);

                // adding person into ArrayList
                persons.add(person);

                // read next line before looping
                // if end of file reached, line would be null
                line = br.readLine();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return persons;
    }
    private static List<Sections> readSectionsFromCSV(String fileName) {
        List<Sections> sections = new ArrayList<>();
        Path pathToFile = Paths.get(fileName);

        // create an instance of BufferedReader
        // using try with resource, Java 7 feature to close resources
        try ( BufferedReader br = Files.newBufferedReader(pathToFile)) {
            // read the first line from the text file
            String line = br.readLine();
            // loop until all lines are read
            while (line != null) {
                // use string.split to load a string array with the values from
                // each line of
                // the file, using a comma as the delimiter
                String[] attributes = line.split(",");
                //System.out.println(attributes.length);
                Sections section = createSections(attributes);

                // adding person into ArrayList
                sections.add(section);

                // read next line before looping
                // if end of file reached, line would be null
                line = br.readLine();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return sections;
    }
    private static List<Subsections> readSubsectionsFromCSV(String fileName) {
        List<Subsections> subsections = new ArrayList<>();
        Path pathToFile = Paths.get(fileName);

        // create an instance of BufferedReader
        // using try with resource, Java 7 feature to close resources
        try ( BufferedReader br = Files.newBufferedReader(pathToFile)) {
            // read the first line from the text file
            String line = br.readLine();
            // loop until all lines are read
            while (line != null) {
                // use string.split to load a string array with the values from
                // each line of
                // the file, using a comma as the delimiter
                String[] attributes = line.split(",");
                //System.out.println(attributes.length);
                Subsections subsection = createSubsections(attributes);

                // adding person into ArrayList
                subsections.add(subsection);

                // read next line before looping
                // if end of file reached, line would be null
                line = br.readLine();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return subsections;
    }
    private static List<Units> readUnitsFromCSV(String fileName) {
        List<Units> units = new ArrayList<>();
        Path pathToFile = Paths.get(fileName);

        // create an instance of BufferedReader
        // using try with resource, Java 7 feature to close resources
        try ( BufferedReader br = Files.newBufferedReader(pathToFile)) {
            // read the first line from the text file
            String line = br.readLine();
            // loop until all lines are read
            while (line != null) {
                // use string.split to load a string array with the values from
                // each line of
                // the file, using a comma as the delimiter
                String[] attributes = line.split(",");
                //System.out.println(attributes.length);
                Units unit = createUnits(attributes);

                // adding person into ArrayList
                units.add(unit);

                // read next line before looping
                // if end of file reached, line would be null
                line = br.readLine();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return units;
    }
    private static List<Logs> readLogsFromCSV(String fileName) {
        List<Logs> logs = new ArrayList<>();
        Path pathToFile = Paths.get(fileName);

        // create an instance of BufferedReader
        // using try with resource, Java 7 feature to close resources
        try ( BufferedReader br = Files.newBufferedReader(pathToFile)) {
            // read the first line from the text file
            String line = br.readLine();
            // loop until all lines are read
            while (line != null) {
                // use string.split to load a string array with the values from
                // each line of
                // the file, using a comma as the delimiter
                String[] attributes = line.split(",");
                //System.out.println(attributes.length);
                Logs log = createLogs(attributes);

                // adding person into ArrayList
                logs.add(log);

                // read next line before looping
                // if end of file reached, line would be null
                line = br.readLine();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return logs;
    }
    private static List<Events> readEventsFromCSV(String fileName) {
        List<Events> events = new ArrayList<>();
        Path pathToFile = Paths.get(fileName);

        // create an instance of BufferedReader
        // using try with resource, Java 7 feature to close resources
        try ( BufferedReader br = Files.newBufferedReader(pathToFile)) {
            // read the first line from the text file
            String line = br.readLine();
            // loop until all lines are read
            while (line != null) {
                // use string.split to load a string array with the values from
                // each line of
                // the file, using a comma as the delimiter
                String[] attributes = line.split(",");
                //System.out.println(attributes.length);
                Events event = createEvents(attributes);

                // adding person into ArrayList
                events.add(event);

                // read next line before looping
                // if end of file reached, line would be null
                line = br.readLine();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return events;
    }
    private static List<Metadata> readMetadataFromCSV(String fileName) {
        List<Metadata> metadatas = new ArrayList<>();
        Path pathToFile = Paths.get(fileName);

        // create an instance of BufferedReader
        // using try with resource, Java 7 feature to close resources
        try ( BufferedReader br = Files.newBufferedReader(pathToFile)) {
            // read the first line from the text file
            String line = br.readLine();
            // loop until all lines are read
            while (line != null) {
                // use string.split to load a string array with the values from
                // each line of
                // the file, using a comma as the delimiter
                String[] attributes = line.split(",");
                //System.out.println(attributes.length);
                Metadata metadata = createMetadata(attributes);

                // adding person into ArrayList
                metadatas.add(metadata);

                // read next line before looping
                // if end of file reached, line would be null
                line = br.readLine();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return metadatas;
    }
    private static List<Chapters> readChaptersFromCSV(String fileName) {
        List<Chapters> chapters = new ArrayList<>();
        Path pathToFile = Paths.get(fileName);

        // create an instance of BufferedReader
        // using try with resource, Java 7 feature to close resources
        try ( BufferedReader br = Files.newBufferedReader(pathToFile)) {
            // read the first line from the text file
            String line = br.readLine();
            // loop until all lines are read
            while (line != null) {
                // use string.split to load a string array with the values from
                // each line of
                // the file, using a comma as the delimiter
                String[] attributes = line.split(",");
                //System.out.println(attributes.length);
                Chapters chapter = createChapters(attributes);

                // adding person into ArrayList
                chapters.add(chapter);

                // read next line before looping
                // if end of file reached, line would be null
                line = br.readLine();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return chapters;
    }
    

    private static Courses createCourses(String[] metadata) {
        String code = metadata[0];
        String label = metadata[1];
        String startDate = metadata[2];
        String endDate = metadata[3];
        // create and return courses of this metadata
        return new Courses(code, label, startDate, endDate);
    }
    private static Persons createPersons(String[] metadata) {
        String user_name = metadata[0];
        String course_code = metadata[1];
        String role = metadata[2];
        return new Persons(user_name, course_code, role);
    }
    private static Sections createSections(String[] metadata) {
        String id = metadata[0];
        String display_name = metadata[1];
        String startDate = metadata[2];
        String time = metadata[3];
        boolean visible_to_staff_only = new Boolean(metadata[4]);
        String type = metadata[5];
        String course_code = metadata[6];
        return new Sections(id, display_name, startDate, time, visible_to_staff_only, type, course_code);
    }
    private static Subsections createSubsections(String[] metadata) {
        String section_id = metadata[0];
        String id = metadata[1];
        String display_name = metadata[2];
        String startDate = metadata[3];
        String time = metadata[4];
        boolean visible_to_staff_only = new Boolean(metadata[5]);
        String grader_type = metadata[6];
        String type = metadata[7];
        String course_code = metadata[8];
        return new Subsections(section_id, id, display_name, startDate, time, visible_to_staff_only, grader_type, type, course_code);
    }
    private static Units createUnits(String[] metadata) {
        String subsection_id = metadata[0];
        String id = metadata[1];
        String display_name = metadata[2];
        String type = metadata[3];
        String course_code = metadata[4];
        return new Units(subsection_id, id, display_name, type, course_code);
    }
    private static Logs createLogs(String[] metadata) {
        String name = metadata[0];
        String date = metadata[1];
        String time = metadata[2];
        return new Logs(name, date, time);
    }
    private static Events createEvents(String[] metadata) {
        String log_name = metadata[0];
        String type = metadata[1];

        String block_id;
        if (metadata[2].isEmpty() == true) {
            block_id = "vacio";
        }else{
            block_id = String.valueOf(metadata[2]);
        }

        String username = metadata[3];
        String time = metadata[4];
        String eventsType = metadata[5];
        
        String id2;
        if (metadata[6].isEmpty() == true) {
            id2 = "vacio";
        }else{
            id2 = metadata[6];
        }
        
        String push_notification_selected;
        if (metadata[7].isEmpty() == true) {
            push_notification_selected = "vacio";
        }else{
            push_notification_selected = metadata[7];
        }
        
        String push_notification_enabled;
        if (metadata[8].isEmpty() == true) {
            push_notification_enabled = "vacio";
        }else{
            push_notification_enabled = metadata[8];
        }
        
        String display_name;
        if (metadata[9].isEmpty() == true) {
            display_name = "vacio";
        }else{
            display_name = metadata[9];
        }
        
        String category;
        if (metadata[10].isEmpty() == true) {
            category = "vacio";
        }else{
            category = metadata[10];
        }
        
        String parent_locator;
        if (metadata[11].isEmpty() == true) {
            parent_locator = "vacio";
        }else{
            parent_locator = metadata[11];
        }
        
        String html;
        if (metadata[12].isEmpty() == true) {
            html = "vacio";
        }else{
            html = metadata[12];
        }
        
        String publish;
        if (metadata[13].isEmpty() == true) {
            publish = "vacio";
        }else{
            publish = metadata[13];
        }
        
        String grader_type;
        if (metadata[14].isEmpty() == true) {
            grader_type = "vacio";
        }else{
            grader_type = metadata[14];
        }
        
        String tab_title;
        if (metadata[15].isEmpty() == true) {
            tab_title = "vacio";
        }else{
            tab_title = metadata[15];
        }
        
        String role;
        if (metadata[16].isEmpty() == true) {
            role = "vacio";
        }else{
            role = metadata[16];
        }
        
        String org;
        if (metadata[17].isEmpty() == true) {
            org = "vacio";
        }else{
            org = metadata[17];
        }
        
        String course_id;
        if (metadata[18].isEmpty() == true) {
            course_id = "vacio";
        }else{
            course_id = metadata[18];
        }
        
        String run;
        if (metadata[19].isEmpty() == true) {
            run = "vacio";
        }else{
            run = metadata[19];
        }
        
        String language;
        if (metadata[20].isEmpty() == true) {
            language = "vacio";
        }else{
            language = metadata[20];
        }
        
        String startDate;
        if (metadata[21].isEmpty() == true) {
            startDate = "vacio";
        }else{
            startDate = metadata[21];
        }
        
        String endDate;
        if (metadata[22].isEmpty() == true) {
            endDate = "vacio";
        }else{
            endDate = metadata[22];
        }
        
        String enrollment_start;
        if (metadata[23].isEmpty() == true) {
            enrollment_start = "vacio";
        }else{
            enrollment_start = metadata[23];
        }
        
        String enrollment_end;
        if (metadata[24].isEmpty() == true) {
            enrollment_end = "vacio";
        }else{
            enrollment_end = metadata[24];
        }
        
        String syllabus;
        if (metadata[25].isEmpty() == true) {
            syllabus = "vacio";
        }else{
            syllabus = metadata[25];
        }
        
        String overview;
        if (metadata[26].isEmpty() == true) {
            overview = "vacio";
        }else{
            overview = metadata[26];
        }
                
        String bloque;
        if (metadata[28].isEmpty() == true) {
            bloque = "vacio";
        }else{
            bloque = metadata[27];
        }
        
        int id = Integer.parseInt(metadata[28]);
        

        return new Events(log_name, type, block_id, username, time, eventsType, id2, push_notification_selected, push_notification_enabled, display_name, category, parent_locator, html, publish, grader_type, tab_title, role, org, course_id, run, language, startDate, endDate, enrollment_start, enrollment_end, syllabus,overview, id, bloque);
    }
    private static Metadata createMetadata(String[] metadata) {
        String event_id = metadata[0];
        String start = metadata[1];
        String due = metadata[2];
        String visible_to_staff_only = metadata[3];
        String display_name = metadata[4];
        int id = Integer.parseInt(metadata[5]);
        
        return new Metadata(event_id, start, due, visible_to_staff_only, display_name, id);
    }
    private static Chapters createChapters(String[] metadata) {
        String event_id = metadata[0];
        String title = metadata[1];
        String url = metadata[2];        
        int id = Integer.parseInt(metadata[3]);
        
        return new Chapters(event_id, title, url,id);
    }
}


class Courses{
    private String code;    
    private String label;
    private String startDate;
    private String endDate;
    private String description;

    public Courses(String code, String label, String startDate, String endDate, String description) {
        this.code = code;
        this.label = label;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
    }

    public Courses(String code, String label, String startDate, String endDate) {
        this.code = code;
        this.label = label;
        this.startDate = startDate;
        this.endDate = endDate;
    }
    

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }    
    
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Courses{" + "code=" + code + ", label=" + label + ", startDate=" + startDate + ", endDate=" + endDate + ", description=" + description + '}';
    }   
}

class Persons{
    private String user_name;
    private String course_code;
    private String role;

    public Persons(String user_name, String course_code, String role) {
        this.user_name = user_name;
        this.course_code = course_code;
        this.role = role;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getCourse_code() {
        return course_code;
    }

    public void setCourse_code(String course_code) {
        this.course_code = course_code;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "Persons{" + "user_name=" + user_name + ", course_code=" + course_code + ", role=" + role + '}';
    }
}

class Sections {
    private String id;
    private String display_name;
    private String startDate;
    private String time;
    private Boolean visible_to_staff_only;
    private String type;
    private String course_code;

    public Sections(String id, String display_name, String startDate, String time, Boolean visible_to_staff_only, String type, String course_code) {
        this.id = id;
        this.display_name = display_name;
        this.startDate = startDate;
        this.time = time;
        this.visible_to_staff_only = visible_to_staff_only;
        this.type = type;
        this.course_code = course_code;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Boolean getVisible_to_staff_only() {
        return visible_to_staff_only;
    }

    public void setVisible_to_staff_only(Boolean visible_to_staff_only) {
        this.visible_to_staff_only = visible_to_staff_only;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCourse_code() {
        return course_code;
    }

    public void setCourse_code(String course_code) {
        this.course_code = course_code;
    }

    @Override
    public String toString() {
        return "Sections{" + "id=" + id + ", display_name=" + display_name + ", startDate=" + startDate + ", time=" + time + ", visible_to_staff_only=" + visible_to_staff_only + ", type=" + type + ", course_code=" + course_code + '}';
    }
}

class Subsections {

    private String section_id;
    private String id;
    private String display_name;
    private String startDate;
    private String time;
    private Boolean visible_to_staff_only;
    private String grader_type;
    private String type;
    private String course_code;

    public Subsections(String section_id, String id, String display_name, String startDate, String time, Boolean visible_to_staff_only, String grader_type, String type, String course_code) {
        this.section_id = section_id;
        this.id = id;
        this.display_name = display_name;
        this.startDate = startDate;
        this.time = time;
        this.visible_to_staff_only = visible_to_staff_only;
        this.grader_type = grader_type;
        this.type = type;
        this.course_code = course_code;
    }

    public String getSection_id() {
        return section_id;
    }

    public void setSection_id(String section_id) {
        this.section_id = section_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Boolean getVisible_to_staff_only() {
        return visible_to_staff_only;
    }

    public void setVisible_to_staff_only(Boolean visible_to_staff_only) {
        this.visible_to_staff_only = visible_to_staff_only;
    }

    public String getGrader_type() {
        return grader_type;
    }

    public void setGrader_type(String grader_type) {
        this.grader_type = grader_type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCourse_code() {
        return course_code;
    }

    public void setCourse_code(String course_code) {
        this.course_code = course_code;
    }

    @Override
    public String toString() {
        return "Subsections{" + "section_id=" + section_id + ", id=" + id + ", display_name=" + display_name + ", startDate=" + startDate + ", time=" + time + ", visible_to_staff_only=" + visible_to_staff_only + ", grader_type=" + grader_type + ", type=" + type + ", course_code=" + course_code + '}';
    }
}

class Units{
    private String subsection_id;
    private String id;
    private String display_name;
    private String type;
    private String course_code;

    public Units(String subsection_id, String id, String display_name, String type, String course_code) {
        this.subsection_id = subsection_id;
        this.id = id;
        this.display_name = display_name;
        this.type = type;
        this.course_code = course_code;
    }

    public String getSubsection_id() {
        return subsection_id;
    }

    public void setSubsection_id(String subsection_id) {
        this.subsection_id = subsection_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCourse_code() {
        return course_code;
    }

    public void setCourse_code(String course_code) {
        this.course_code = course_code;
    }

    @Override
    public String toString() {
        return "Units{" + "subsection_id=" + subsection_id + ", id=" + id + ", display_name=" + display_name + ", type=" + type + ", course_code=" + course_code + '}';
    }
}

class Logs{
    private String name;
    private String date;
    private String time;

    public Logs(String name, String date, String time) {
        this.name = name;
        this.date = date;
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "Logs{" + "name=" + name + ", date=" + date + ", time=" + time + '}';
    }
}

class Events{
    private int id;
    private String log_name;
    private String type;
    private String block_id;
    private String username;
    private String time;
    private String requestType;
    private String eventsType;
    private String published_on;
    private String explanatory_message;
    private Boolean graded;
    private Boolean has_explicit_staff_lock;
    private String children;
    private String data;
    private String id2;
    private String push_notification_selected;
    private String push_notification_enabled;
    private String display_name;
    private String category;
    private String parent_locator;
    private String html;
    private String publish;
    private String grader_type;
    private String tab_title;
    private String role;
    private String org;
    private String course_id;
    private String run;
    private String language;
    private String startDate;
    private String endDate;
    private String enrollment_start;
    private String enrollment_end;
    private String syllabus;
    private String short_description;
    private String overview;
    private String bloque;

    public Events(String log_name, String type, String block_id, String username, String time, String eventsType, String id2, String push_notification_selected, String push_notification_enabled, String display_name, String category, String parent_locator, String html, String publish, String grader_type, String tab_title, String role, String org, String course_id, String run, String language, String startDate, String endDate, String enrollment_start, String enrollment_end, String syllabus, String overview, int id, String bloque) {      
        this.log_name = log_name;
        this.type = type;
        this.block_id = block_id;
        this.username = username;
        this.time = time;
        this.eventsType = eventsType;
        this.id2 = id2;
        this.push_notification_selected = push_notification_selected;
        this.push_notification_enabled = push_notification_enabled;
        this.display_name = display_name;
        this.category = category;
        this.parent_locator = parent_locator;
        this.html = html;
        this.publish = publish;
        this.grader_type = grader_type;
        this.tab_title = tab_title;
        this.role = role;
        this.org = org;
        this.course_id = course_id;
        this.run = run;
        this.language = language;
        this.startDate = startDate;
        this.endDate = endDate;
        this.enrollment_start = enrollment_start;
        this.enrollment_end = enrollment_end;
        this.syllabus = syllabus;
        this.overview = overview;
        this.id = id;
        this.bloque = bloque;
    }
    
    public Events(int id, String log_name, String type, String block_id, String username, String time, String requestType, String eventsType, String published_on, String explanatory_message, Boolean graded, Boolean has_explicit_staff_lock, String children, String data, String id2, String push_notification_selected, String push_notification_enabled, String display_name, String category, String parent_locator, String html, String publish, String grader_type, String tab_title, String role, String org, String course_id, String run, String language, String startDate, String endDate, String enrollment_start, String enrollment_end, String syllabus, String short_description, String overview) {
        this.id = id;
        this.log_name = log_name;
        this.type = type;
        this.block_id = block_id;
        this.username = username;
        this.time = time;
        this.requestType = requestType;
        this.eventsType = eventsType;
        this.published_on = published_on;
        this.explanatory_message = explanatory_message;
        this.graded = graded;
        this.has_explicit_staff_lock = has_explicit_staff_lock;
        this.children = children;
        this.data = data;
        this.id2 = id2;
        this.push_notification_selected = push_notification_selected;
        this.push_notification_enabled = push_notification_enabled;
        this.display_name = display_name;
        this.category = category;
        this.parent_locator = parent_locator;
        this.html = html;
        this.publish = publish;
        this.grader_type = grader_type;
        this.tab_title = tab_title;
        this.role = role;
        this.org = org;
        this.course_id = course_id;
        this.run = run;
        this.language = language;
        this.startDate = startDate;
        this.endDate = endDate;
        this.enrollment_start = enrollment_start;
        this.enrollment_end = enrollment_end;
        this.syllabus = syllabus;
        this.short_description = short_description;
        this.overview = overview;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLog_name() {
        return log_name;
    }

    public void setLog_name(String log_name) {
        this.log_name = log_name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBlock_id() {
        return block_id;
    }

    public void setBlock_id(String block_id) {
        this.block_id = block_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public String getEventsType() {
        return eventsType;
    }

    public void setEventsType(String eventsType) {
        this.eventsType = eventsType;
    }

    public String getPublished_on() {
        return published_on;
    }

    public void setPublished_on(String published_on) {
        this.published_on = published_on;
    }

    public String getExplanatory_message() {
        return explanatory_message;
    }

    public void setExplanatory_message(String explanatory_message) {
        this.explanatory_message = explanatory_message;
    }

    public Boolean getGraded() {
        return graded;
    }

    public void setGraded(Boolean graded) {
        this.graded = graded;
    }

    public Boolean getHas_explicit_staff_lock() {
        return has_explicit_staff_lock;
    }

    public void setHas_explicit_staff_lock(Boolean has_explicit_staff_lock) {
        this.has_explicit_staff_lock = has_explicit_staff_lock;
    }

    public String getChildren() {
        return children;
    }

    public void setChildren(String children) {
        this.children = children;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getId2() {
        return id2;
    }

    public void setId2(String id2) {
        this.id2 = id2;
    }

    public String getPush_notification_selected() {
        return push_notification_selected;
    }

    public void setPush_notification_selected(String push_notification_selected) {
        this.push_notification_selected = push_notification_selected;
    }

    public String getPush_notification_enabled() {
        return push_notification_enabled;
    }

    public void setPush_notification_enabled(String push_notification_enabled) {
        this.push_notification_enabled = push_notification_enabled;
    }
    
    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getParent_locator() {
        return parent_locator;
    }

    public void setParent_locator(String parent_locator) {
        this.parent_locator = parent_locator;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public String getPublish() {
        return publish;
    }

    public void setPublish(String publish) {
        this.publish = publish;
    }

    public String getGrader_type() {
        return grader_type;
    }

    public void setGrader_type(String grader_type) {
        this.grader_type = grader_type;
    }

    public String getTab_title() {
        return tab_title;
    }

    public void setTab_title(String tab_title) {
        this.tab_title = tab_title;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getOrg() {
        return org;
    }

    public void setOrg(String org) {
        this.org = org;
    }

    public String getCourse_id() {
        return course_id;
    }

    public void setCourse_id(String course_id) {
        this.course_id = course_id;
    }

    public String getRun() {
        return run;
    }

    public void setRun(String run) {
        this.run = run;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getEnrollment_start() {
        return enrollment_start;
    }

    public void setEnrollment_start(String enrollment_start) {
        this.enrollment_start = enrollment_start;
    }

    public String getEnrollment_end() {
        return enrollment_end;
    }

    public void setEnrollment_end(String enrollment_end) {
        this.enrollment_end = enrollment_end;
    }

    public String getSyllabus() {
        return syllabus;
    }

    public void setSyllabus(String syllabus) {
        this.syllabus = syllabus;
    }

    public String getShort_description() {
        return short_description;
    }

    public void setShort_description(String short_description) {
        this.short_description = short_description;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getBloque() {
        return bloque;
    }

    public void setBloque(String bloque) {
        this.bloque = bloque;
    }

    @Override
    public String toString() {
        return "Events{" + "id=" + id + ", log_name=" + log_name + ", type=" + type + ", block_id=" + block_id + ", username=" + username + ", time=" + time + ", requestType=" + requestType + ", eventsType=" + eventsType + ", published_on=" + published_on + ", explanatory_message=" + explanatory_message + ", graded=" + graded + ", has_explicit_staff_lock=" + has_explicit_staff_lock + ", children=" + children + ", data=" + data + ", id2=" + id2 + ", push_notification_selected=" + push_notification_selected + ", push_notification_enabled=" + push_notification_enabled + ", display_name=" + display_name + ", category=" + category + ", parent_locator=" + parent_locator + ", html=" + html + ", publish=" + publish + ", grader_type=" + grader_type + ", tab_title=" + tab_title + ", role=" + role + ", org=" + org + ", course_id=" + course_id + ", run=" + run + ", language=" + language + ", startDate=" + startDate + ", endDate=" + endDate + ", enrollment_start=" + enrollment_start + ", enrollment_end=" + enrollment_end + ", syllabus=" + syllabus + ", short_description=" + short_description + ", overview=" + overview + ", bloque=" + bloque + '}';
    }
}

class Metadata {
    private String event_id;
    private String start;
    private String due;
    private String visible_to_staff_only;
    private String display_name;
    private int id;

    public Metadata(String event_id, String start, String due, String visible_to_staff_only, String display_name, int id) {
        this.event_id = event_id;
        this.start = start;
        this.due = due;
        this.visible_to_staff_only = visible_to_staff_only;
        this.display_name = display_name;
        this.id = id;
    }

    public String getEvent_id() {
        return event_id;
    }

    public void setEvent_id(String event_id) {
        this.event_id = event_id;
    }

        public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getDue() {
        return due;
    }

    public void setDue(String due) {
        this.due = due;
    }

    public String getVisible_to_staff_only() {
        return visible_to_staff_only;
    }

    public void setVisible_to_staff_only(String visible_to_staff_only) {
        this.visible_to_staff_only = visible_to_staff_only;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Metadata{" + "event_id=" + event_id + ", start=" + start + ", due=" + due + ", visible_to_staff_only=" + visible_to_staff_only + ", display_name=" + display_name + ", id=" + id + '}';
    }
    
}

class Chapters {
    private String event_id;
    private String title;
    private String url;    
    private int id;

    public Chapters(String event_id, String title, String url, int id) {
        this.event_id = event_id;
        this.title = title;
        this.url = url;
        this.id = id;
    }

    public String getEvent_id() {
        return event_id;
    }

    public void setEvent_id(String event_id) {
        this.event_id = event_id;
    }
    
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Chapters{" + "event_id=" + event_id + ", title=" + title + ", url=" + url + ", id=" + id + '}';
    }
    
}