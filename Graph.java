public class Graph{ // Graph implementation

	public int [][] adjacencyMatrix; // edge weights in miles
	public int [][] timeToTravel; // edge weights in minutes
	public GraphNode [] vertices; // array of all Graph Nodes/vertices
	public int size; // total size 
	public int currSize = 0; // used only when constructing the graph

	public class GraphNode{ // graph node stores city and state data of a vertice

		private String city, state;

		public GraphNode(String city, String state){
			this.city = city;
			this.state = state;
		}

		public String get(){
			return city + " " + state;
		}

	}

	public Graph(int size){
		adjacencyMatrix = new int[size][size];
		timeToTravel = new int[size][size];
		vertices = new GraphNode[size];
		this.size = size;
	}

	public int size(){
		return size;
	}

	public void addNode(String city, String state){
		vertices[currSize] = new GraphNode(city, state);
		currSize++;
	}

	public void addEdge(String city1, String state1, String city2, String state2, int miles, int minutes){

		int ind1 = this.getIndex(city1 + " " + state1);
		int ind2 = this.getIndex(city2 + " " + state2);

		this.adjacencyMatrix[ind1][ind2] = miles;
		this.adjacencyMatrix[ind2][ind1] = miles;

		this.timeToTravel[ind1][ind2] = minutes;
		this.timeToTravel[ind2][ind1] = minutes;
		
	}

	public int getIndex(String city, String state){
		for(int i = 0; i < vertices.length; i ++){
			if(vertices[i].get().equals(city + " " + state))
				return i; 
		}
		return -1;
	}

	public int getIndex(String cityState){
		for(int i = 0; i < vertices.length; i ++){
			if(vertices[i].get().equals(cityState))
				return i; 
		}
		return -1;
	}

}