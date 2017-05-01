/**
 * Package for printing elements as JSON suitable for
 * displaying as a graph in Cytoscape.
 * Based on the xml package and uses some classes such as
 * PrintContext from there.
 * Note the JSON structure is flat: A list of nodes and list of edges, rather than
 * the XML tree structure which more or less directly reflects the class relationships
 * in a model.
 *
 *
 * TODO: Make Tail-Recursive to overcome Stack Overflows (e.g. on COSD). Probably best to make PrintContext into a stack.
 * TODO: implement more ValidationRule and DataType printing, probably by passing an extra parameter, a map of data, to super.printElement
 * TODO: use RelationshipType.sourceToDestination as relationship name
 * @see org.modelcatalogue.core.xml
 */
package org.modelcatalogue.core.cytoscape.json
