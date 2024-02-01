/*
 * Bao Lab 2017
 */

package application_src.application_model.loaders;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import javax.vecmath.Color4f;

import com.interactivemesh.jfx.importer.obj.ObjModelImporter;

import javafx.scene.paint.Material;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;

import application_src.MainApp;
import application_src.application_model.threeD.subscenegeometry.SceneElementMeshView;
import customnode.WavefrontLoader;

import static java.lang.Integer.MIN_VALUE;

import java.awt.Color;

/**
 * Builder for scene element mesh geometries to be placed in the 3D subscene
 */
public class GeometryLoader {

    private static final String OBJ_EXTENSION = ".obj";
    private static final String VERTEX_LINE = "v";
    private static final String FACE_LINE = "f";
	private static final String NAME_LINE = "g";
	private static final String S_LINE = "s";
	private static final String USEMTL_LINE = "usemtl";
	private static final String MTLLIB_LINE = "mtllib";

    /**
     * Checks to see if a spefified resource exists in the shapes archive.
     *
     * @param resourcePath the resource path to check, without the .obj extension
     * @return the effective start time at which this geometry exists, {@link Integer#MIN_VALUE} if the resource does
     * not exist
     */
    public static int getEffectiveStartTime(
            String resourcePath,
            final int startTime,
            final int endTime) {
        resourcePath = "/" + resourcePath;
        URL url = MainApp.class.getResource(resourcePath + OBJ_EXTENSION);
        if (url != null) {
            return startTime;
        } else {
            // check for obj file with a time
            for (int time = startTime; time <= endTime; time++) {
                url = MainApp.class.getResource(resourcePath + "_t" + time + OBJ_EXTENSION);
                if (url != null) {
                    return time;
                }
            }
        }
        return MIN_VALUE;
    }

    /**
     * Builds a 3D mesh from a file
     *
     * @param resourcePath the resource path to check, without the .obj extension
     * @return the 3D mesh
     */
    public static SceneElementMeshView loadOBJ(String resourcePath) {
        SceneElementMeshView meshView = null;
        resourcePath = "/" + resourcePath + OBJ_EXTENSION;
        final URL url = MainApp.class.getResource(resourcePath);

        if (url != null) {
            return createMeshFromManualLoader(url);
//            return createMeshFromLibraryLoader(url);
        } else {
            return meshView;
        }

    }

    private static SceneElementMeshView createMeshFromManualLoader(URL url) {
        SceneElementMeshView meshView = null;
        final List<double[]> coords = new ArrayList<>();
        final List<int[]> faces = new ArrayList<>();
        try (final InputStreamReader streamReader = new InputStreamReader(url.openStream());
             final BufferedReader reader = new BufferedReader(streamReader)) {
            String line;
            StringTokenizer tokenizer;
            String v;
            String f;
            String g = "";
            String s;
            String usemtl="";
            String mtllib = "";
            String lineType;
            HashMap<String, Color4f> colorHashMap = new HashMap<String, Color4f>();

            while ((line = reader.readLine()) != null) {
                // processUrl each line in the obj file
                if (line.length() < 2) {
                    continue;
                }
                lineType = line.split(" ")[0];
                switch (lineType) {
	                case MTLLIB_LINE: {

	                	mtllib = line.replaceAll("^"+lineType+" (.*)", "$1").trim();

	                	File mtlFile = new File(new File(url.getFile()).getParent() + File.separator + mtllib);
	                	
	                    HashMap<String, Color4f> chm = WavefrontLoader.readMaterials(mtlFile);
	                    colorHashMap.putAll(chm);
	                	
	                	break;
	                }
	                case NAME_LINE: {

	                	g = line.replaceAll("^"+lineType+" (.*)", "$1");
	                	break;
	                }
	                case USEMTL_LINE: {

	                	usemtl = line.replaceAll("^"+lineType+" (.*)", "$1").trim();
	                	
	                	break;
	                }
	                case S_LINE: {

	                }
                    case VERTEX_LINE: {
                        // processUrl vertex lines
                        v = line.replaceAll("^"+lineType+" (.*)", "$1");
                        double[] vertices = new double[3];
                        int counter = 0;
                        tokenizer = new StringTokenizer(v);
                        while (tokenizer.hasMoreTokens()) {
                            vertices[counter++] = Double.parseDouble(tokenizer.nextToken());
                        }
                        // make sure good line
                        if (counter == 3) {
                            coords.add(vertices);
                        }
                        break;
                    }
                    case FACE_LINE: {
                        // processUrl face lines
                        f = line.replaceAll("^"+lineType+" (.*)", "$1");

                        tokenizer = new StringTokenizer(f);

                        if (tokenizer.countTokens() == 3) {
                            int[] faceCoords = new int[3];
                            int counter = 0;

                            while (tokenizer.hasMoreTokens()) {
                                faceCoords[counter++] = Integer.parseInt(tokenizer.nextToken());
                            }
                            if (counter == 3) {
                                faces.add(faceCoords);
                            }
                            break;
                        }
                    }
                    default:
                        break;
                }
            }
            meshView = new SceneElementMeshView(createMesh(coords, faces));
            meshView.setCellName(g);
            meshView.setColors(new ArrayList<javafx.scene.paint.Color>());
            if (!mtllib.equals("") && !usemtl.equals("")) {
            	double redD = (double)(colorHashMap.get(usemtl).x);
            	double grnD = (double)(colorHashMap.get(usemtl).y);
            	double bluD = (double)(colorHashMap.get(usemtl).z);
            	double opaD = 1.0-(double)(colorHashMap.get(usemtl).w);
            	javafx.scene.paint.Color mtlColor = javafx.scene.paint.Color.color(redD, grnD, bluD, opaD);
            	meshView.getColors().add(mtlColor);
            }
            meshView.pickOutMarkerPoints(coords);
            
        } catch (IOException e) {
            e.printStackTrace();
        }

        return meshView;
    }


    private static SceneElementMeshView createMeshFromLibraryLoader(URL url) {
        SceneElementMeshView meshView = null;
        ObjModelImporter objImporter = new ObjModelImporter();
        try {
            objImporter.read(url);
        } catch (Exception e) {
            System.out.println("exception thrown building mesh: " + url.toString()+"\n"+e);
//            System.exit(0);
            //e.printStackTrace();
        }

        MeshView[] mvs = objImporter.getImport();
        //System.out.println("size of mvs: " + mvs.length);
        if (mvs.length == 1) {
            meshView = new SceneElementMeshView(mvs[0]);
        } else {
            System.out.println("meshviews has size: " + mvs.length);
        }

        return meshView;
    }

    /**
     * Builds the mesh from the loaded vertex coordinates and faces in the file
     */
    private static TriangleMesh createMesh(final List<double[]> coords, final List<int[]> faces) {
        final TriangleMesh mesh = new TriangleMesh();
        int counter = 0;
        int texCounter = 0;
        final float stripeSeparation = 1500;
        float[] coordinates = new float[(coords.size() * 3)];
        float[] texCoords = new float[(coords.size() * 2)];
        for (double[] coord : coords) {
            for (int j = 0; j < 3; j++) {
                coordinates[counter++] = (float) coord[j];
            }
            texCoords[texCounter++] = 0;
            texCoords[texCounter++] = ((float) coord[0] / stripeSeparation) * 200;
        }
        mesh.getPoints().addAll(coordinates);
        mesh.getTexCoords().addAll(texCoords);
        counter = 0;
        int[] faceCoords = new int[(faces.size() * 3) * 2];
        for (int[] face : faces) {
            for (int j = 0; j < 3; j++) {
                faceCoords[counter++] = face[j] - 1;
                faceCoords[counter++] = face[j] - 1;
                // texture coordinate - face syntax:
                // p0, t0, p1,
                // t1, p2, t2
            }
        }
        mesh.getFaces().addAll(faceCoords);
        return mesh;
    }
}