# Ray Tracer

This is an experimental ray tracing-based renderer for basic geometric primitives as well as arbitrary triangle meshes, written in Java.



## UI

Currently, the simple UI supports loading an XML scene description, and rendering it into a digital image which is then displayed.



## Scene Description Language

The **scene description language** that the renderer can understand is a subset of the XML. The scene description XML file must have the structure defined by the following set of rules:

- The root node is named ```elements```.
- The children of the root node are two nodes named ```shapes``` and ```lights```, respectively.
- The ```shapes``` node describes the shapes in the scene and contains any nonnegative integer number of nodes, each of which describes one shape.
- The ```lights``` node describes the lights in the scene and contains any nonnegative integer number of nodes, each of which describes one light.
- The nodes describing shapes and lights (children of ```shapes``` or ```lights```) must have structure as defined in their corresponding paragraph below.

### Supported Shapes

#### Sphere

#### Plane

#### Triangle

#### Triangle Mesh

#### Box

#### Example of a Valid Scene Description

```
<elements>
   <shapes>
       <sphere>
           <center>(2,2,10)</center>
           <radius>3</radius>
           <color>(255,0,0)</color>
       </sphere>

       <plane>
           <normal>(0,1,0)</normal>
           <point>(0,-20,0)</point>
           <color>(151,235,145)</color>
       </plane>

       <triangle>
           <vertexA>(-8,3,40)</vertexA>
           <vertexB>(-6,-13,21)</vertexB>
           <vertexC>(-11,-13,21)</vertexC>
           <color>(0,165,80)</color>
           <material>green-rubber</material>
       </triangle>

       <triangle-mesh>
           <path-to-obj-file>src/main/resources/meshes/pawnFlatShadedWithNormals.obj</path-to-obj-file>
           <model-transform>
               <scale>(2.5,2.5,2.5)</scale>
               <translate>(1.5,-3,15)</translate>
           </model-transform>
           <color>(218,165,32)</color>
           <material>gold</material>
      </triangle-mesh>

      <box-mesh>
           <minX>0</minX>
           <maxX>1</maxX>
           <minY>0</minY>
           <maxY>1</maxY>
           <minZ>0</minZ>
           <maxZ>1</maxZ>
           <model-transform>
               <scale>(1,1,1)</scale>
               <translate>(1.5,-1.5,4.8)</translate>
               <rotateX>-20</rotateX>
               <rotateZ>45</rotateZ>
               <rotateY>10</rotateY>
               <translate>(0,0,0.2)</translate>
           </model-transform>
           <color>(255,255,0)</color>
       </box-mesh>
   </shapes>

   <lights>
       <point-light>
           <position>(-10,10,-5)</position>
           <color>(255,255,0)</color>
           <intensity>0.6</intensity>
       </point-light>

       <sphere-light>
           <position>(-12,15,0)</position>
           <color>(255,255,255)</color>
           <intensity>1</intensity>
           <radius>0.5</radius>
       </sphere-light>
   </lights>
</elements>
```


## Instructions for Running

To run the project, first clone the repository locally: 

```
git clone https://github.com/losmi247/ray-tracer
```

Open the project in an IDE and make sure _Maven_ imports the dependencies from the ```pom.xml``` file - try running ```mvn clean install``` if it doesn't.

Then navigate to the main UI file at relative path 

```
/src/main/java/ui/MainApp.java
```

and run the following command to start the _JavaFX_ app using _Maven_:

```
mvn javafx:run -f "/home/milos/Desktop/Nesto/VSCode/ray-tracer/pom.xml"
```
