package ch.eiafr.rdf;

import java.io.File;

import org.eclipse.rdf4j.common.iteration.Iterations;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.model.vocabulary.FOAF;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.eclipse.rdf4j.model.vocabulary.XMLSchema;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.query.resultio.text.tsv.SPARQLResultsTSVWriter;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryResult;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.sail.memory.MemoryStore;

import com.fasterxml.jackson.annotation.JacksonInject.Value;

public class RessourceInformatique {

	//Modélisation RDFS
	static void buildOntology(Repository rep) {

	    RepositoryConnection conn = rep.getConnection();
	    ValueFactory f = rep.getValueFactory();
	    String namespace = "http://eia-fr.ch/ressource-informatique/";

	    
	    IRI rdfsSubClassOf = RDFS.SUBCLASSOF;

	    // Class Definition
	    IRI informaticien = f.createIRI(namespace, "informaticien");
	    IRI ordinateur = f.createIRI(namespace, "Ordinateur");
	    IRI laptop = f.createIRI(namespace, "Laptop");
	    IRI desktop = f.createIRI(namespace, "Desktop");
	    IRI serveur = f.createIRI(namespace, "Serveur");
	    IRI equipementReseau = f.createIRI(namespace, "EquipementReseau");
	    IRI peripherique = f.createIRI(namespace, "Peripherique");
	    IRI licence = f.createIRI(namespace, "Licence");
	    IRI logiciel = f.createIRI(namespace, "Logiciel");
	    IRI emplacement = f.createIRI(namespace, "Emplacement");

	    // Object properties
	    IRI administre = f.createIRI(namespace, "administre");
	    IRI connecteA = f.createIRI(namespace, "connecteA");
	    IRI gerePar = f.createIRI(namespace, "gerePar");
	    IRI dispose = f.createIRI(namespace, "dispose");
	    IRI installeSur = f.createIRI(namespace, "installeSur");
	    
	    IRI estLocalise = f.createIRI(namespace, "estLocalise");

	    try {
	    	// Ajout des domains et range d'object properties et data properties
	    	
	        conn.add(informaticien, rdfsSubClassOf, FOAF.PERSON);
	    
	        conn.add(laptop, rdfsSubClassOf, ordinateur);
	        conn.add(desktop, rdfsSubClassOf, ordinateur);
	   
	        
	        conn.add(informaticien, administre, ordinateur);
	        conn.add(informaticien, administre, serveur);
	        conn.add(informaticien, administre, equipementReseau);

	        conn.add(serveur, connecteA, equipementReseau);
	        conn.add(serveur, dispose, licence);
	        conn.add(serveur, estLocalise, emplacement);
	        
	        conn.add(equipementReseau, dispose, licence);
	        conn.add(equipementReseau, estLocalise, emplacement);
	        
	        conn.add(peripherique, estLocalise, emplacement);
	        conn.add(peripherique, connecteA, equipementReseau);
	        
	        conn.add(ordinateur, connecteA, equipementReseau);
	        conn.add(ordinateur, estLocalise, emplacement);
	        conn.add(ordinateur, dispose, licence);
	        
	        conn.add(logiciel, installeSur, ordinateur);
	        conn.add(logiciel, installeSur, serveur);
	        conn.add(logiciel, dispose, licence);
	        
	        conn.add(licence, gerePar, informaticien);
	        
	        
	        RepositoryResult<Statement> statements = conn.getStatements(null, null, null, true);
	        Model model = Iterations.addAll(statements, new LinkedHashModel());

	        model.setNamespace("rdf", RDF.NAMESPACE);
	        model.setNamespace("rdfs", RDFS.NAMESPACE);
	        model.setNamespace("xsd", XMLSchema.NAMESPACE);
	        model.setNamespace("foaf", FOAF.NAMESPACE);

	        Rio.write(model, System.out, RDFFormat.TURTLE);
	    } finally {
	        conn.close();
	        rep.shutDown();
	        System.out.println("\nConnection closed");
	    }
	}

	 //Création de l'élément de type Informaticien
    static void createIndividualInformaticien(Repository rep, String className, String nom, String idEmploye, String finContrat, IRI classIRI) {

        RepositoryConnection conn = rep.getConnection();
        ValueFactory f = rep.getValueFactory();

        IRI instanceIRI = f.createIRI("http://eia-fr.ch/ressource-informatique#" + nom);
        IRI rdfType = RDF.TYPE;
        IRI rdfsLabel = RDFS.LABEL;

        conn.add(instanceIRI, rdfType, classIRI);
        conn.add(instanceIRI, rdfsLabel, f.createLiteral(nom));

         System.out.println("L'individu suivant est créé: " + nom + " de classe " + className);

        conn.close();
    }
    
  //Création de l'élément de type Serveur
    static void createIndividualServeur(Repository rep, String className, String nomEq, String numeroSerie, String marque, IRI classIRI) {
    	
    	RepositoryConnection conn = rep.getConnection ();
        ValueFactory f = conn.getValueFactory();

        IRI instanceIRI = f.createIRI("http://eia-fr.ch/ressource-informatique#" + nomEq);
        IRI rdfType = RDF.TYPE;
        IRI rdfsLabel = RDFS.LABEL;

        conn.add(instanceIRI, rdfType, classIRI);
        conn.add(instanceIRI, rdfsLabel, f.createLiteral(nomEq));

        System.out.println("L'individu suivant est créé: " + nomEq +  "  de classe " + className +" ,numéro de série: " +numeroSerie);
        conn.close();
    }
    //Création de l'élément de type Ordinateur
    
    static void createIndividualOrdinateur(Repository rep, String nomEq, String className, String numeroSerie, String marque, IRI classIRI) {
        	
        	RepositoryConnection conn = rep.getConnection ();
            ValueFactory f = rep.getValueFactory();

            IRI instanceIRI = f.createIRI("http://eia-fr.ch/ressource-informatique#" + nomEq);
            IRI rdfType = RDF.TYPE;
            IRI rdfsLabel = RDFS.LABEL;

            conn.add(instanceIRI, rdfType, classIRI);
            conn.add(instanceIRI, rdfsLabel, f.createLiteral(nomEq));

            System.out.println("L'individu suivant est créé: ordinateur S/N" + nomEq +  "  de classe " + className +" ,numéro de maruqe: " +marque);
            
            conn.close();
        }
    
  //Création de l'élément de type Périphérique
    static void createIndividualPeripherique(Repository rep, String className, String nomPeriph, String usage, String marque, IRI classIRI) {
    	
    	RepositoryConnection conn = rep.getConnection ();
        ValueFactory f = conn.getValueFactory();

        IRI instanceIRI = f.createIRI("http://eia-fr.ch/ressource-informatique#" + nomPeriph);
        IRI rdfType = RDF.TYPE;
        IRI rdfsLabel = RDFS.LABEL;

        conn.add(instanceIRI, rdfType, classIRI);
        conn.add(instanceIRI, rdfsLabel, f.createLiteral(nomPeriph));

        System.out.println("L'individu suivant est créé: " + nomPeriph +  "  de classe " + className );
        conn.close();
    }


  //Création de l'élément de type Equipement réseau
  static void createIndividualEquipementReseau(Repository rep, String className, String nomEq, String typeEq, String marque, int nbrePort, IRI classIRI) {
  	
  	RepositoryConnection conn = rep.getConnection ();
      ValueFactory f = conn.getValueFactory();

      IRI instanceIRI = f.createIRI("http://eia-fr.ch/ressource-informatique#" + nomEq);
      IRI rdfType = RDF.TYPE;
      IRI rdfsLabel = RDFS.LABEL;

      conn.add(instanceIRI, rdfType, classIRI);
      conn.add(instanceIRI, rdfsLabel, f.createLiteral(nomEq));

     System.out.println("L'individu suivant est créé: " + nomEq +  "  de classe " + className +" ,type: " +typeEq);
      conn.close();
  }

//Création del'élément de type Logiciel
static void createIndividualLogiciel(Repository rep, String className, String nomLog, String usage, IRI classIRI) {
	
	RepositoryConnection conn = rep.getConnection ();
    ValueFactory f = conn.getValueFactory();

    IRI instanceIRI = f.createIRI("http://eia-fr.ch/ressource-informatique#" + nomLog);
    IRI rdfType = RDF.TYPE;
    IRI rdfsLabel = RDFS.LABEL;

    conn.add(instanceIRI, rdfType, classIRI);
    conn.add(instanceIRI, rdfsLabel, f.createLiteral(nomLog));

   System.out.println("L'individu suivant est créé: " + nomLog+  "  de classe " + className );
    conn.close();
}

//Création de l'élément de type Licence
static void createIndividualLicence(Repository rep, String className, String nomLic, String dateExp, IRI classIRI) {
	
	RepositoryConnection conn = rep.getConnection ();
    ValueFactory f = conn.getValueFactory();

    IRI instanceIRI = f.createIRI("http://eia-fr.ch/ressource-informatique#" + nomLic);
    IRI rdfType = RDF.TYPE;
    IRI rdfsLabel = RDFS.LABEL;

    conn.add(instanceIRI, rdfType, classIRI);
    conn.add(instanceIRI, rdfsLabel, f.createLiteral(nomLic));

   System.out.println("L'individu suivant est créé: " + nomLic+  "  de classe " + className );
    conn.close();
}


//Création de l'élément Emplacement
static void createIndividualEmplacement(Repository rep, String className, String nomEmpl, String adresse, IRI classIRI) {
	
	RepositoryConnection conn = rep.getConnection ();
  ValueFactory f = conn.getValueFactory();

  IRI instanceIRI = f.createIRI("http://eia-fr.ch/ressource-informatique#" + nomEmpl);
  IRI rdfType = RDF.TYPE;
  IRI rdfsLabel = RDFS.LABEL;

  conn.add(instanceIRI, rdfType, classIRI);
  conn.add(instanceIRI, rdfsLabel, f.createLiteral(nomEmpl));

 System.out.println("L'individu suivant est créé: " + nomEmpl+  "  de classe " + className );
  conn.close();
}
  


  
static void createIndividuals(Repository rep) {
        try (RepositoryConnection conn = rep.getConnection()) {
            ValueFactory f = rep.getValueFactory();
            
           //Ajout des données  RDF type informaticien
            createIndividualInformaticien(rep, "informaticien", "Joseph Doe", "12345","31/12/2025", f.createIRI("http://eia-fr.ch/ressource-informatique#doe") );
            createIndividualInformaticien(rep, "informaticien", "Bernard Dadie", "345678","01/10/2030", f.createIRI("http://eia-fr.ch/ressource-informatique#dadie"));
            
          //Ajout des données  RDF type ordinateur
            createIndividualOrdinateur(rep, "ordinateur", "Lap01","SN-D2345EW1","DELL Inspiron 24", f.createIRI("http://eia-fr.ch/ressource-informatique#lapo1"));
            createIndividualOrdinateur(rep, "ordinateur", "Desk01","SN-LEN4523RT","LENOVO ThinkPad T16 ", f.createIRI("http://eia-fr.ch/ressource-informatique#desk02"));
          //Ajout des données  RDF type  serveur
            createIndividualServeur(rep, "serveur", "SERV-PRINT-01", "SN-3452DRFG","HPE ProLiant DL380",f.createIRI("http://eia-fr.ch/ressource-informatique#serveur1"));
            createIndividualServeur(rep, "serveur", "SERV-FILE-05", "SN-3452ABCE","Dell PowerEdge R760",f.createIRI("http://eia-fr.ch/ressource-informatique#serveur2"));
          //Ajout des données  RDF type Equipement réseau
            createIndividualEquipementReseau(rep, "equipementReseau", "ACCESS-SWITCH-001", "Swicth","Cisco Catalyst 9300", 48, f.createIRI("http://eia-fr.ch/ressource-informatique#er01"));
            createIndividualEquipementReseau(rep, "equipementReseau", "ROUTER-RT-001", "Routeur","Cisco ISR4400",0, f.createIRI("http://eia-fr.ch/ressource-informatique#er02")); 
            ////Ajout des données  RDF type périphérique
            createIndividualPeripherique(rep, "peripherique", "Per01","Scanner","HP N9120", f.createIRI("http://eia-fr.ch/ressource-informatique#per01"));
            createIndividualPeripherique(rep, "peripherique", "Per02","Imprimante","HP  MFP M578f", f.createIRI("http://eia-fr.ch/ressource-informatique#per02"));
          //Ajout des données  RDF type logiciel
           createIndividualLogiciel(rep, "logiciel", "Norton XD-300","Antivirus", f.createIRI("http://eia-fr.ch/ressource-informatique#log01"));
           createIndividualLogiciel(rep, "logiciel", "Adobe XD","Graphisme", f.createIRI("http://eia-fr.ch/ressource-informatique#log02"));
         //Ajout des données  RDF type licence
           createIndividualLicence(rep, "licence", "Plesk", "08/03/2024",f.createIRI("http://eia-fr.ch/ressource-informatique#lic01"));
           createIndividualLicence(rep, "licence", "LanSweepper", "01/10/2028",f.createIRI("http://eia-fr.ch/ressource-informatique#lic02"));
         //Ajout des données  RDF type  Emplacement
           createIndividualEmplacement(rep, "emplacement",  "Bana Building", "Avenue de l Etang,1219 Vernier", f.createIRI("http://eia-fr.ch/ressource-informatique#Empl01"));
            
        }
    }
    

   

    
   






/* Requête 1 : Requête permettant d'afficher  les noms et ID des informaticiens, classer les noms affichés par ordre alphabétique. */

public static void getInformaticien(Repository rep) {

    String queryString =
            "PREFIX ex: <http://eia-fr.ch/ressource-informatique#> \n" +
            "SELECT DISTINCT ?nom ?idEmploye WHERE { \n" +
            "  ?x rdf:type ex:informaticien. \n" +
            "  ?x ex:nom ?nom. \n" +
            "  ?x ex:idEmploye ?idEmploye.\n" +
            "} \n" +
            "ORDER BY ASC(?nom)";

    try (RepositoryConnection conn = rep.getConnection()) {
        TupleQuery query = conn.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
        try (TupleQueryResult result = query.evaluate()) {
            while (result.hasNext()) {
                System.out.println(result.next());
            }
        }
    }
}



/*requête 2 : Requête permettant d'afficher  les noms des informaticiens  dont le contrat prendra fin avant le 31/12/2026. La date de fin de contrat sera aussi affichée.*/

public static void getFinContrat(Repository rep) {

    String queryString =
            "PREFIX ex: <http://eia-fr.ch/ressource-informatique#> \n" +
            "SELECT DISTINCT  ?nom ?finContrat WHERE  { \n" +
            "  ?x rdf:type ex:informaticien. \n" +
            "  ?x ex:nom ?nom. \n" +
            "  ?x ex:finContrat ?finContrat.\n" +
            "    FILTER (?finContrat < 31/12/2026). \n" +
            "} \n" ;

    try (RepositoryConnection conn = rep.getConnection()) {
        TupleQuery query = conn.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
        try (TupleQueryResult result = query.evaluate()) {
            while (result.hasNext()) {
                System.out.println(result.next());
            }
        }
    }
}

/*Requête 3 : Requête permettant d'afficher les noms et types d'équipement réseau avec le nombre de port si l'information est disponible*/
    

public static void getTypeEq(Repository rep) {

    String queryString =
            "PREFIX ex: <http://eia-fr.ch/ressource-informatique#> \n" +
            "SELECT DISTINCT ?nomEq ?typeEq ?nbrePort  WHERE  { \n" +
            "  ?x rdf:type ex:equipement_Reseau. \n" +
            "  ?x ex:nomEq ?nomEq. \n" +
            "  ?x ex:typeEq ?typeEq.\n" +
            "    OPTIONAL { ?nbrePort ex:nbrPort  ?x. \n" +
            "} \n" +
            "} \n"
            ;

    try (RepositoryConnection conn = rep.getConnection()) {
        TupleQuery query = conn.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
        try (TupleQueryResult result = query.evaluate()) {
            while (result.hasNext()) {
                System.out.println(result.next());
            }
        }
    }
}

//Requête 4 : Requête pour afficher marque et numéro d'ordinateurs ou serveurs

public static void getSerialNumber(Repository rep) {

    String queryString =
            "PREFIX ex: <http://eia-fr.ch/ressource-informatique#> \n" +
            "SELECT DISTINCT ?marque ?numeroSerie WHERE  { \n" +
            "  { ?x rdf:type ex:serveur. }\n" +
            "  UNION\n" +
            "  { ?x rdf:type ex:Ordinateur. }\n" +
            "  ?x ex:marque ?marque. \n" +
            "  ?x ex:numeroSerie ?numeroSerie. \n" +
            "} ";

    try (RepositoryConnection conn = rep.getConnection()) {
        TupleQuery query = conn.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
        try (TupleQueryResult result = query.evaluate()) {
            while (result.hasNext()) {
                System.out.println(result.next());
            }
        }
    }
}



//Requête 5 : Afficher les marques de périphériques de type imprimante

public static void getPrinter(Repository rep) {

    String queryString =
            "PREFIX ex: <http://eia-fr.ch/ressource-informatique#> \n" +
            "SELECT DISTINCT ?marque ?usage WHERE   { \n" +
            "  ?x rdf:type ex:Peripherique. \n" +
            "  ?x ex:marque ?marque. \n" +
            "  ?x ex:usage ?usage.\n" +
             "} ";

    try (RepositoryConnection conn = rep.getConnection()) {
        TupleQuery query = conn.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
        try (TupleQueryResult result = query.evaluate()) {
            while (result.hasNext()) {
                System.out.println(result.next());
            }
        }
    }
}

//Afficher les licences avec les dates d'expiration

public static void getLicenceInfo(Repository rep) {

    String queryString =
            "PREFIX ex: <http://eia-fr.ch/ressource-informatique#> \n" +
            "SELECT DISTINCT ?nomLic ?dateExp WHERE  { \n" +
            "  ?x rdf:type ex:Licence. \n" +
            "  ?x ex:nomLic ?nomLic. \n" +
            "  ?x ex:dateExp ?dateExp.\n" +
            "} ";

    try (RepositoryConnection conn = rep.getConnection()) {
        TupleQuery query = conn.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
        try (TupleQueryResult result = query.evaluate()) {
            while (result.hasNext()) {
                System.out.println(result.next());
            }
        }
    }
}

    public static void main(String[] args) {
        File dataDir = new File("C:\\temp\\myRepositoryProject5\\");
        Repository rep = new SailRepository(new MemoryStore(dataDir));
   //    RepositoryConnection conn = rep.getConnection();
   //     ValueFactory f = rep.getValueFactory();
        


        buildOntology(rep);
        createIndividuals(rep);
        getInformaticien(rep);
        getFinContrat(rep);
        getTypeEq(rep);
        getSerialNumber(rep);
        getPrinter(rep);
        getLicenceInfo(rep);
        
        

        

        
    }
}
