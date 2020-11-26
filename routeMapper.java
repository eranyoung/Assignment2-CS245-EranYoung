import java.util.*;
import java.io.*;
import java.lang.*;

public class RouteMapper{

	Graph routeMap;
	Map<String, String[]> attractions = new HashMap<String, String[]>();

	public RouteMapper(File attractions, File roads, int size){
		routeMap = new Graph(size);
		initGraph(roads);
		initGraphEdges(roads);
		initAttractions(attractions);
	}

	public void initGraph(File roads){
		Set<String[]> cityTracker = new HashSet<String[]>();
		BufferedReader csvReader;
		try{
			csvReader = new BufferedReader(new FileReader(roads));
			String row;
			try{
				while((row = csvReader.readLine()) != null){
					String [] data = row.split(",");
					String [] cityState1 = data[0].split(" ");
					String [] cityState2 = data[1].split(" ");

					if(cityState1.length > 2){
						for(int i = 1; i < cityState1.length - 1; i ++){
							cityState1[0] = cityState1[0] + " " + cityState1[i];
						}
						cityState1[1] = cityState1[cityState1.length-1];
					}
					if(cityState2.length > 2){
						for(int i = 1; i < cityState2.length - 1; i ++){
							cityState2[0] = cityState2[0] + " " + cityState2[i];
						}
						cityState2[1] = cityState2[cityState2.length-1];
					}



					if(!cityTracker.contains(cityState1)){
						this.routeMap.addNode(cityState1[0], cityState1[1]);
						cityTracker.add(cityState1);
					}
					if(!cityTracker.contains(cityState2)){
						this.routeMap.addNode(cityState2[0], cityState2[1]);
						cityTracker.add(cityState2);
					}
				}
			}catch(IOException c){
				System.out.println(c);
			}
		}catch(FileNotFoundException e){
			System.out.println(e);
		}
	}

	public void initGraphEdges(File roads){
		BufferedReader csvReader;
		try{
			csvReader = new BufferedReader(new FileReader(roads));
			String row;
			try{
				while((row = csvReader.readLine()) != null){
					String [] data = row.split(",");
					String [] cityState1 = data[0].split(" ");
					String [] cityState2 = data[1].split(" ");

					if(cityState1.length > 2){
						for(int i = 1; i < cityState1.length - 1; i ++){
							cityState1[0] = cityState1[0] + " " + cityState1[i];
						}
						cityState1[1] = cityState1[cityState1.length-1];
					}
					if(cityState2.length > 2){
						for(int i = 1; i < cityState2.length - 1; i ++){
							cityState2[0] = cityState2[0] + " " + cityState2[i];
						}
						cityState2[1] = cityState2[cityState2.length-1];
					}

					this.routeMap.addEdge(cityState1[0], cityState1[1], cityState2[0], cityState2[1], Integer.parseInt(data[2]), Integer.parseInt(data[3]));
				}
			}catch(IOException c){
				System.out.println(c);
			}
		}catch(FileNotFoundException e){
			System.out.println(e);
		}
		
	}

	public void initAttractions(File attractions){
		BufferedReader csvReader;
		try{
			csvReader = new BufferedReader(new FileReader(attractions));
			String row; 
			try{
				while((row = csvReader.readLine()) != null){
					String [] data = row.split(",");
					String [] cityState = data[1].split(" ");

					if(cityState.length > 2){
						for(int i = 1; i < cityState.length - 1; i ++){
							cityState[0] = cityState[0] + " " + cityState[i];
						}
						cityState[1] = cityState[cityState.length-1];
					}

					this.attractions.put(data[0], cityState);
				}
			}catch(IOException c){
				System.out.println(c);
			}
		}catch(FileNotFoundException e){
			System.out.println(e);
		}
	}

	public List<String> route(String startingCity, String startingState, String endingCity, String endingState, List<String> attractions){
		List<String> route;
		Queue<String> routeQueue = new LinkedList<String>();
		routeQueue.add(startingCity + " " + startingState);
		for(int i = 0; i < attractions.size(); i ++){
			String [] location = this.attractions.get(attractions.get(i));
			routeQueue.add(location[0] + " " + location[1]);
		}
		routeQueue.add(endingCity + " " + endingState);
		route = shortestPath(routeQueue);
		return route;

	}

	public List<String> shortestPath(Queue<String> routeQueue){
		List<String> route = new ArrayList<String>();
		

		int [] parents;

		Queue<String> s = new LinkedList<String>();

		while(routeQueue.size() > 1){
			String start = routeQueue.remove();
			int startingIndex = this.routeMap.getIndex(start);
			int endingIndex = this.routeMap.getIndex(routeQueue.peek());
			parents = dijkstras(startingIndex, endingIndex);
			walkParents(s, endingIndex, parents);
		}

		while(!s.isEmpty()){
			route.add(s.remove());
		}

		return route;
	}

	public void walkParents(Queue<String> s, int end, int [] parents){
		if(end == -1)
			return;
		walkParents(s, parents[end], parents);
		s.add(this.routeMap.vertices[end].get());

	}

	private int leastCostUnknownVertex(int [] dist, boolean [] visited){
		int min = Integer.MAX_VALUE, min_index = -1; 
  
        for (int v = 0; v < dist.length; v++) 
            if (visited[v] == false && dist[v] <= min) { 
                min = dist[v]; 
                min_index = v; 
            } 
        return min_index;
	}

	public int [] dijkstras(int source, int ending){

		int [][] adjacencyMatrix = this.routeMap.adjacencyMatrix;
		int size = this.routeMap.size();

		int [] sDists = new int[size];
		boolean [] visited = new boolean[size];

		for(int i = 0; i < size; i ++){
			sDists[i] = Integer.MAX_VALUE;
			visited[i] = false;
		}

		sDists[source] = 0;
		int [] parents = new int[size];
		parents[source] = -1;
		
		for(int i = 0; i < size; i++){
			int l = leastCostUnknownVertex(sDists, visited);
			if(l == -1)
				break;
			int sDist = sDists[l];
			visited[l] = true;
			for(int v = 0; v < size; v++){
				int weight = adjacencyMatrix[l][v];

				if(weight > 0 && ((sDist + weight) < sDists[v])){
					parents[v] = l;
					sDists[v] = sDist + weight;
				}
			}
			if(l == ending)
				break;
		}

		return parents;
	}

	

	public void printRoute(List<String> route){
		System.out.println("Route:");
		System.out.println(route.get(0));
		for(int i = 1; i < route.size(); i ++){
			System.out.println("=>\n" + route.get(i));
		}
	}


	public static void main(String [] args){
		File attractions = new File("attractions.csv");
		File roads = new File("roads.csv");
		int size = 0;
		Set<String[]> cityTracker = new HashSet<String[]>();
		BufferedReader csvReader;
		try{
			csvReader = new BufferedReader(new FileReader(roads));
			String row;
			try{
				while((row = csvReader.readLine()) != null){
					String [] data = row.split(",");
					String [] cityState1 = data[0].split(" ");
					String [] cityState2 = data[1].split(" ");
					if(!cityTracker.contains(cityState1)){
						cityTracker.add(cityState1);
						size++;
					}
					if(!cityTracker.contains(cityState2)){
						cityTracker.add(cityState2);
						size++;
					}
				}
			}catch(IOException c){
				System.out.println(c);
			}
		}catch(FileNotFoundException e){
			System.out.println(e);
		}
		RouteMapper r = new RouteMapper(attractions, roads, size);

		List<String> a = new ArrayList<String>();
		a.add("Alcatraz");
		a.add("Disney World");
		List<String> s = r.route("San Francisco", "CA", "Orlando", "FL", a);
		r.printRoute(s);

	}
}