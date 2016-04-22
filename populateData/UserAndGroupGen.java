// package com.company;

/**
 * Created by Xiaokai on 4/16/2016.
 */


/**
 * Created by xiaokaiwang on 4/15/16.
 */
//STEP 1. Import required packages
import java.sql.*;
import java.text.SimpleDateFormat;
import java.io.*;
import java.util.*;
public class UserAndGroupGen {
    // JDBC driver name and database URL

    private static final String JDBC_DRIVER = "oracle.jdbc.driver.OracleDriver";
    private static final String DB_URL = "jdbc:oracle:thin:@class3.cs.pitt.edu:1521:dbclass";
    private static final String USER = "xiw69";
    private static final String PASS = "3799662";

    public static void main(String[] args) {
        Connection conn = null;
        Statement stmt = null;
        String str = "INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('dolor.quam@Sedmolestie.com','Daquan','Mullen',TO_DATE('1986-04-16', 'YYYY-mm-dd'));" +
                "INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('erat.Sed.nunc@aliquam.edu','Jameson','Austin',TO_DATE('1986-04-16', 'YYYY-mm-dd'));" +
                "INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('lorem@magna.co.uk','Beau','Stevens',TO_DATE('1986-04-16', 'YYYY-mm-dd'));" +
                "INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('mus.Donec@liberoest.co.uk','Duncan','Bradshaw',TO_DATE('1986-04-16', 'YYYY-mm-dd'));" +
                "INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('parturient.montes.nascetur@odio.net','Stuart','Gates',TO_DATE('1986-04-16', 'YYYY-mm-dd'));" +
                "INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('orci.adipiscing@Aliquam.com','Shoshana','Ross',TO_DATE('1986-04-16', 'YYYY-mm-dd'));" +
                "INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('Vivamus.sit@purusmaurisa.net','Xavier','Melton',TO_DATE('1986-04-16', 'YYYY-mm-dd'));" +
                "INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('urna@metuseuerat.co.uk','Slade','Mcbride',TO_DATE('1986-04-16', 'YYYY-mm-dd'));" +
                "INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('Sed.diam@semperauctorMauris.co.uk','Allegra','Welch',TO_DATE('1986-04-16', 'YYYY-mm-dd'));" +
                "INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('mauris@congueelitsed.co.uk','Olympia','Gray',TO_DATE('1986-04-16', 'YYYY-mm-dd'));" +
                "INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('consequat@Duis.com','Valentine','Juarez',TO_DATE('1986-04-16', 'YYYY-mm-dd'));" +
                "INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('Nullam@tinciduntnibh.com','Macon','Rios',TO_DATE('1986-04-16', 'YYYY-mm-dd'));" +
                "INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('faucibus.Morbi@arcuacorci.co.uk','Matthew','Holcomb',TO_DATE('1986-04-16', 'YYYY-mm-dd'));" +
                "INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('dignissim.magna.a@Quisqueporttitoreros.ca','Ian','Atkinson',TO_DATE('1986-04-16', 'YYYY-mm-dd'));" +
                "INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('non.cursus.non@egestasa.net','Harriet','Gillespie',TO_DATE('1986-04-16', 'YYYY-mm-dd'));" +
                "INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('ornare@orciUt.edu','Yeo','Patel',TO_DATE('1986-04-16', 'YYYY-mm-dd'));" +
                "INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('Mauris.magna@Sed.edu','Erica','Cooley',TO_DATE('1986-04-16', 'YYYY-mm-dd'));" +
                "INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('Nunc.ullamcorper@dolorsit.ca','Lucian','Phelps',TO_DATE('1986-04-16', 'YYYY-mm-dd'));" +
                "INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('et.magna.Praesent@nequepellentesque.org','Anjolie','Mcpherson',TO_DATE('1986-04-16', 'YYYY-mm-dd'));" +
                "INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('Mauris.magna@lacusvariuset.edu','Caryn','Reed',TO_DATE('1986-04-16', 'YYYY-mm-dd'));" +
                "INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('neque@augueid.net','Quin','Hendrix',TO_DATE('1986-04-16', 'YYYY-mm-dd'));" +
                "INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('Maecenas@senectus.edu','Uma','Cox',TO_DATE('1986-04-16', 'YYYY-mm-dd'));" +
                "INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('leo@eunibhvulputate.ca','Aimee','Hyde',TO_DATE('1986-04-16', 'YYYY-mm-dd'));" +
                "INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('enim.consequat.purus@tinciduntnequevitae.org','Joelle','Noble',TO_DATE('1986-04-16', 'YYYY-mm-dd'));" +
                "INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('hendrerit.neque.In@ullamcorpernisl.co.uk','Joy','Bray',TO_DATE('1986-04-16', 'YYYY-mm-dd'));" +
                "INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('convallis.in@perinceptos.net','Prescott','Moreno',TO_DATE('1986-04-16', 'YYYY-mm-dd'));" +
                "INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('erat.Sed.nunc@musProinvel.net','Justine','Mcknight',TO_DATE('1986-04-16', 'YYYY-mm-dd'));" +
                "INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('Cum.sociis.natoque@velitegestaslacinia.co.uk','Galena','Holloway',TO_DATE('1986-04-16', 'YYYY-mm-dd'));" +
                "INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('in@Cras.ca','Idona','Merritt',TO_DATE('1986-04-16', 'YYYY-mm-dd'));" +
                "INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('a.mi.fringilla@accumsaninterdumlibero.com','Jocelyn','Holloway',TO_DATE('1986-04-16', 'YYYY-mm-dd'));" +
                "INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('scelerisque.lorem@nullaDonecnon.co.uk','Yeo','Barrera',TO_DATE('1986-04-16', 'YYYY-mm-dd'));" +
                "INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('Aliquam.adipiscing@hendrerit.org','Aidan','Wiggins',TO_DATE('1986-04-16', 'YYYY-mm-dd'));" +
                "INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('Sed.auctor@Etiamligula.edu','Gavin','Mercado',TO_DATE('1986-04-16', 'YYYY-mm-dd'));" +
                "INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('ad.litora.torquent@nonegestas.com','Yuri','Levine',TO_DATE('1986-04-16', 'YYYY-mm-dd'));" +
                "INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('quis@nequeIn.net','Wyatt','Everett',TO_DATE('1986-04-16', 'YYYY-mm-dd'));" +
                "INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('mus.Aenean.eget@sedsapien.co.uk','Brennan','Solomon',TO_DATE('1986-04-16', 'YYYY-mm-dd'));" +
                "INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('Donec.fringilla@id.ca','Blaze','Alston',TO_DATE('1986-04-16', 'YYYY-mm-dd'));" +
                "INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('dictum.placerat.augue@facilisis.co.uk','Oliver','Mckenzie',TO_DATE('1986-04-16', 'YYYY-mm-dd'));" +
                "INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('Sed.id.risus@lacusQuisquepurus.co.uk','Lucian','Morin',TO_DATE('1986-04-16', 'YYYY-mm-dd'));" +
                "INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('Morbi.quis.urna@libero.ca','September','Parsons',TO_DATE('1986-04-16', 'YYYY-mm-dd'));" +
                "INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('adipiscing.elit.Etiam@necmaurisblandit.com','Rooney','Hahn',TO_DATE('1986-04-16', 'YYYY-mm-dd'));" +
                "INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('tempus@Sedmolestie.ca','Walter','Glenn',TO_DATE('1986-04-16', 'YYYY-mm-dd'));" +
                "INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('accumsan.sed@lacus.co.uk','Griffith','Jefferson',TO_DATE('1986-04-16', 'YYYY-mm-dd'));" +
                "INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('velit.Cras.lorem@Suspendissetristiqueneque.com','Troy','Maddox',TO_DATE('1986-04-16', 'YYYY-mm-dd'));" +
                "INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('eget@nisiMaurisnulla.org','Phoebe','Wynn',TO_DATE('1986-04-16', 'YYYY-mm-dd'));" +
                "INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('metus.In@utpellentesqueeget.ca','Andrew','Dunn',TO_DATE('1986-04-16', 'YYYY-mm-dd'));" +
                "INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('at.lacus.Quisque@cubiliaCuraeDonec.edu','Yen','King',TO_DATE('1986-04-16', 'YYYY-mm-dd'));" +
                "INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('non.vestibulum.nec@tempusrisusDonec.edu','Thor','Hays',TO_DATE('1986-04-16', 'YYYY-mm-dd'));" +
                "INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('mauris.elit.dictum@Quisque.net','Dai','Luna',TO_DATE('1986-04-16', 'YYYY-mm-dd'));" +
                "INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('malesuada@temporarcuVestibulum.ca','Phillip','Vinson',TO_DATE('1986-04-16', 'YYYY-mm-dd'));" +
                "INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('in.aliquet.lobortis@mauris.edu','Cameron','Solis',TO_DATE('1986-04-16', 'YYYY-mm-dd'));" +
                "INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('fringilla.est.Mauris@velvulputate.org','Porter','Harris',TO_DATE('1986-04-16', 'YYYY-mm-dd'));" +
                "INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('ultricies.adipiscing@nondapibusrutrum.net','Hadley','Atkins',TO_DATE('1986-04-16', 'YYYY-mm-dd'));" +
                "INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('gravida@risusodioauctor.com','Dieter','Rice',TO_DATE('1986-04-16', 'YYYY-mm-dd'));" +
                "INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('Phasellus.nulla@malesuadafringilla.org','Melodie','Kidd',TO_DATE('1986-04-16', 'YYYY-mm-dd'));" +
                "INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('tincidunt@Duis.ca','Jael','Wilkinson',TO_DATE('1986-04-16', 'YYYY-mm-dd'));" +
                "INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('urna.et.arcu@condimentumeget.co.uk','John','Campbell',TO_DATE('1986-04-16', 'YYYY-mm-dd'));" +
                "INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('dolor.sit.amet@odio.co.uk','Yeo','Hancock',TO_DATE('1986-04-16', 'YYYY-mm-dd'));" +
                "INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('aliquam@at.org','Hedley','Shelton',TO_DATE('1986-04-16', 'YYYY-mm-dd'));" +
                "INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('magnis.dis@eget.co.uk','Wyatt','Hancock',TO_DATE('1986-04-16', 'YYYY-mm-dd'));" +
                "INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('Nunc.mauris@Maurismolestie.org','Colleen','Graves',TO_DATE('1986-04-16', 'YYYY-mm-dd'));" +
                "INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('mauris.eu@Cras.org','Herman','Kane',TO_DATE('1986-04-16', 'YYYY-mm-dd'));" +
                "INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('ullamcorper.magna@loremeu.net','Zeph','Santos',TO_DATE('1986-04-16', 'YYYY-mm-dd'));" +
                "INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('in.lobortis.tellus@morbi.co.uk','Quail','Yang',TO_DATE('1986-04-16', 'YYYY-mm-dd'));" +
                "INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('sit.amet@etarcu.co.uk','Freya','Thomas',TO_DATE('1986-04-16', 'YYYY-mm-dd'));" +
                "INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('lacinia@nisi.net','Ignacia','Hudson',TO_DATE('1986-04-16', 'YYYY-mm-dd'));" +
                "INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('diam.vel@semperrutrumFusce.org','Hollee','Hernandez',TO_DATE('1986-04-16', 'YYYY-mm-dd'));" +
                "INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('egestas@odioEtiam.org','Joan','Sloan',TO_DATE('1986-04-16', 'YYYY-mm-dd'));" +
                "INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('quis.arcu.vel@lobortis.ca','Rae','Rosario',TO_DATE('1986-04-16', 'YYYY-mm-dd'));" +
                "INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('arcu.imperdiet.ullamcorper@velitdui.ca','Germane','Sellers',TO_DATE('1986-04-16', 'YYYY-mm-dd'));" +
                "INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('malesuada@idrisus.com','Kenyon','Rogers',TO_DATE('1986-04-16', 'YYYY-mm-dd'));" +
                "INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('Morbi@utcursusluctus.net','Amanda','Mccray',TO_DATE('1986-04-16', 'YYYY-mm-dd'));" +
                "INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('nec@ridiculusmusProin.ca','Hop','Pennington',TO_DATE('1986-04-16', 'YYYY-mm-dd'));" +
                "INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('Vestibulum@arcu.com','Gretchen','Holman',TO_DATE('1986-04-16', 'YYYY-mm-dd'));" +
                "INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('ipsum.primis.in@tellus.edu','Natalie','Hester',TO_DATE('1986-04-16', 'YYYY-mm-dd'));" +
                "INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('libero.Proin.mi@vehiculaPellentesque.ca','Ila','Mckee',TO_DATE('1986-04-16', 'YYYY-mm-dd'));" +
                "INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('vitae.purus.gravida@justo.com','Travis','Beasley',TO_DATE('1986-04-16', 'YYYY-mm-dd'));" +
                "INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('ut.dolor@justo.com','Fritz','Jacobs',TO_DATE('1986-04-16', 'YYYY-mm-dd'));" +
                "INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('ut.sem.Nulla@acmattisornare.org','Colton','Glass',TO_DATE('1986-04-16', 'YYYY-mm-dd'));" +
                "INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('vel.convallis.in@utlacus.co.uk','Ulric','Solomon',TO_DATE('1986-04-16', 'YYYY-mm-dd'));" +
                "INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('vitae.purus@egetmollislectus.com','Ian','Mcdaniel',TO_DATE('1986-04-16', 'YYYY-mm-dd'));" +
                "INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('nascetur.ridiculus.mus@odio.com','Dane','Weber',TO_DATE('1986-04-16', 'YYYY-mm-dd'));" +
                "INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('vehicula.aliquet@faucibus.net','Lana','Klein',TO_DATE('1986-04-16', 'YYYY-mm-dd'));" +
                "INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('mi@penatibuset.ca','Winter','Welch',TO_DATE('1986-04-16', 'YYYY-mm-dd'));" +
                "INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('eget.dictum.placerat@eutempor.co.uk','Erasmus','Morin',TO_DATE('1986-04-16', 'YYYY-mm-dd'));" +
                "INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('vitae.aliquam.eros@risus.ca','Odette','Joyce',TO_DATE('1986-04-16', 'YYYY-mm-dd'));" +
                "INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('Nulla.facilisis@venenatisa.org','Christopher','Torres',TO_DATE('1986-04-16', 'YYYY-mm-dd'));" +
                "INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('nec.luctus@vulputaterisusa.net','Dora','Gibbs',TO_DATE('1986-04-16', 'YYYY-mm-dd'));" +
                "INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('ipsum.nunc.id@elitelit.net','Harrison','Wilkerson',TO_DATE('1986-04-16', 'YYYY-mm-dd'));" +
                "INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('Cras@Praesenteu.com','Sopoline','Abbott',TO_DATE('1986-04-16', 'YYYY-mm-dd'));" +
                "INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('sapien.Aenean@enimsit.edu','Wyoming','Galloway',TO_DATE('1986-04-16', 'YYYY-mm-dd'));" +
                "INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('molestie@sapienimperdiet.edu','Keith','Moody',TO_DATE('1986-04-16', 'YYYY-mm-dd'));" +
                "INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('luctus@acorci.net','Maisie','Mclean',TO_DATE('1986-04-16', 'YYYY-mm-dd'));" +
                "INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('ante.iaculis@nuncullamcorper.org','Lucian','Odom',TO_DATE('1986-04-16', 'YYYY-mm-dd'));" +
                "INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('volutpat.nunc.sit@aliquet.ca','Ariel','Everett',TO_DATE('1986-04-16', 'YYYY-mm-dd'));" +
                "INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('sagittis.semper@atpretium.co.uk','Stewart','Mcdonald',TO_DATE('1986-04-16', 'YYYY-mm-dd'));" +
                "INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('semper.egestas.urna@metus.edu','Serena','Britt',TO_DATE('1986-04-16', 'YYYY-mm-dd'));" +
                "INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('in@idmollis.edu','Audra','Meyer',TO_DATE('1986-04-16', 'YYYY-mm-dd'));" +
                "INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('convallis@congueelit.com','Martin','Joyner',TO_DATE('1986-04-16', 'YYYY-mm-dd'));" +
                "INSERT INTO userTable (email,firstName,lastName,birthday) VALUES ('convallis.ante.lectus@placerataugue.com','Kennedy','Byers',TO_DATE('1986-04-16', 'YYYY-mm-dd'));";
        String[] arr = str.split(";");
        String str2 = "INSERT INTO GroupTable (groupID,name,description,mLimit) VALUES (1,'Nulla Corporation','NC',15);" +
                "INSERT INTO GroupTable (groupID,name,description,mLimit) VALUES (2,'Mi Fringilla Mi Associates','MFMA',20);" +
                "INSERT INTO GroupTable (groupID,name,description,mLimit) VALUES (3,'Sem Pellentesque Ut Company','SPUC',30);" +
                "INSERT INTO GroupTable (groupID,name,description,mLimit) VALUES (4,'Ut Lacus Foundation','ULF',40);" +
                "INSERT INTO GroupTable (groupID,name,description,mLimit) VALUES (5,'Metus Sit Inc.','MSI',30);" +
                "INSERT INTO GroupTable (groupID,name,description,mLimit) VALUES (6,'Velit Institute','2vvvsss',20);" +
                "INSERT INTO GroupTable (groupID,name,description,mLimit) VALUES (7,'Tellus Eu Augue Industries','TEAI',30);" +
                "INSERT INTO GroupTable (groupID,name,description,mLimit) VALUES (8,'Purus Associates','PRA',20);" +
                "INSERT INTO GroupTable (groupID,name,description,mLimit) VALUES (9,'Ac Turpis Egestas Company','ATE',5);" +
                "INSERT INTO GroupTable (groupID,name,description,mLimit) VALUES (10,'Eleifend Nec Malesuada Company','ENM',30);";
        String[] arr2 = str2.split(";");
        try{



            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL,USER,PASS);


            System.out.println("Creating statement...");
            stmt = conn.createStatement();

            for (int i =0;i< arr.length; i++){
                String sql = arr[i];

                stmt.executeUpdate(sql);


            }

            for (int j = 0;j < arr2.length; j++){
                String sql2 = arr2[j];
                stmt.executeUpdate(sql2);
            }



            System.out.println("Finishing...");

            stmt.close();
            conn.close();
        }catch(SQLException se){
            //Handle errors for JDBC
            se.printStackTrace();
        }catch(Exception e){
            //Handle errors for Class.forName
            e.printStackTrace();
        }finally{
            //finally block used to close resources
            try{
                if(stmt!=null)
                    stmt.close();
            }catch(SQLException se2){
            }// nothing we can do
            try{
                if(conn!=null)
                    conn.close();
            }catch(SQLException se){
                se.printStackTrace();
            }
        }
        System.out.println("Goodbye!");
    }
}
