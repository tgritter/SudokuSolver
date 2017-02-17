package sudoku;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;

/**
 * Place for your code.
 */
public class SudokuSolver {
	
	public static int SIZE = 9;
	public static int DOMAINS = SIZE*SIZE;
	
	LinkedList<Domains> validBoards = new LinkedList<Domains>();
	LinkedList<Domains> remainingBoards = new LinkedList<Domains>();
	
	/**
	 * @return names of the authors and their student IDs (1 per line).
	 */
	public String authors() {
		// TODO write it;
		return "TRAVIS GRITTER 23786114";
	}

	/**
	 * Performs constraint satisfaction on the given Sudoku board using Arc Consistency and Domain Splitting.
	 * 
	 * @param board the 2d int array representing the Sudoku board. Zeros indicate unfilled cells.
	 * @return the solved Sudoku board
	 */
	 
	  
	public int[][] solve(int[][] board) throws Exception {
		
		// TODO write it;
		Domains start = new Domains( board );
		validBoards.clear();
		remainingBoards.clear();
		remainingBoards.push(start);
		getValidBoards(start);
		
		//Throw Expection if no solutions or more than one solution 		
		if ( validBoards.size() == 1 ){
			return validBoards.peekFirst().output();
		}
		else if (validBoards.size() == 0){
		throw new Exception("No valid boards");
		} else {
			throw new Exception("More than one valid board");
		}
	}

	// Iterate through all the variables and push valid boards to validBoard list
	 	
	public void getValidBoards(Domains start){
	Domains current = null;
	boolean valid = checkArcConsistency( start );
	while ( !remainingBoards.isEmpty() ){			
			current = remainingBoards.pop();
			valid = checkArcConsistency( current );
			if ( !valid ){
				continue;
			} else if ( validDomain( current ) ) {
				validBoards.push(current);				
			} else {								
				outerLoop:
				for ( int i = 0; i < SIZE; i++ ) {
					for ( int j = 0; j < SIZE; j++ ) {
					//Spilt the domain of a variable is the size of the domain is greater than 1
						if ( current.domain[i][j].size() > 1 ) {
							domainSpilt( current, i, j );
							break outerLoop;
						}
					}
				}			
			}
		}		
	}
	
	
	//Spilt the domain of a variables into 2 parts
	public void domainSpilt( Domains d, int row, int col ){		
		
		Domains firstHalf = new Domains( d );
		Domains secondHalf = new Domains( d );
		LinkedList<Integer> first = new LinkedList<Integer>();
		LinkedList<Integer> second = new LinkedList<Integer>();
		int domainLength = d.domain[row][col].size();
		int middle = domainLength / 2 ;		

		for ( Integer i: d.domain[row][col] ) {
			first.add( new Integer( i ) );
			second.add( new Integer( i ) );
		}

		for ( int i = 0; i < middle; i++ ){
			first.removeLast();
		}

		for ( int i = middle; i < domainLength; i++ ){
			second.removeFirst();
		}

		firstHalf.domain[row][col] = first;
		secondHalf.domain[row][col] = second;

		remainingBoards.push( firstHalf );
		remainingBoards.push( secondHalf );
	}

	// return true if consistent, false if empty
	public boolean checkArcConsistency( Domains d ) {
		boolean done = false;
		while ( !done ) {
			int consistentDomains = 0;
			boolean consistent = false;
			for ( int i = 0; i < SIZE; i++ ){
				for ( int j = 0; j < SIZE; j++ ){
					consistent = checkConsistency( d, i, j );
					if ( consistent ) consistentDomains++;
				}
			}
			if ( consistentDomains == DOMAINS ) done = true;
		}		
		return emptyDomain( d );
	}	

	// Return true if this variable's domain is consistent across all constraints
	public boolean checkConsistency( Domains d, int i, int j ){
		boolean rowValid = validRow( d, i, j );
		boolean colValid = validColumn( d, i, j );		
		boolean blockValid = validBlock( d, i, j );

		return ( rowValid && colValid && blockValid );
	}

	// Return true if row constraint is satisfied
	public boolean validRow( Domains d, int i, int j ){
		boolean valid = true;
		for ( int k = 0; k < SIZE; k++ ){
			if ( k != j ) {
				if ( d.domain[i][k].size() == 1 ) {
					int toRemove = d.domain[i][k].getFirst();
					if ( d.domain[i][j].removeFirstOccurrence( toRemove ) ){						
						valid = false;
					}
				}
			}
		}
		return valid;
	}

	// Return true is column constraint is satisfied
	public boolean validColumn( Domains d, int i, int j ){
		boolean valid = true;
		for ( int k = 0; k < SIZE; k++ ){
			if ( k != i ) {
				if ( d.domain[k][j].size() == 1 ) {
					int toRemove = d.domain[k][j].getFirst();
					if ( d.domain[i][j].removeFirstOccurrence(toRemove) ){						
						valid = false;
					}
				}
			}
		}
		return valid;
	}

	// Return true if block constraint is satisfied
	public boolean validBlock( Domains d, int row, int col ){
		boolean valid = true;
		int rowBlock = 3*(row/3);
		int colBlock = 3*(col/3);

		for ( int i = rowBlock; i < (rowBlock + 3); i++ ){
			for ( int j = colBlock; j < (colBlock + 3); j++ ){
				if ( !( i == row && j == col ) ){
					if ( d.domain[i][j].size() == 1 ){
						int toRemove = d.domain[i][j].getFirst();
						if ( d.domain[row][col].removeFirstOccurrence( toRemove ) ){							
							valid = false;
						}
					}
				}
			}
		}
		return valid;
	}
	
	// Returns false is the domain is empty
	public boolean emptyDomain( Domains d ){
		boolean emptyDomain = true;
		for ( int i = 0; i < SIZE; i++ ){
			for ( int j = 0; j< SIZE; j++ ){
				if ( d.domain[i][j].size() == 0 ) emptyDomain = false;
			}
		}
		return emptyDomain;
	}
		
	// Return true if all domains have a size of 1
	public boolean validDomain( Domains d ){
		boolean solution = true;
		for ( int i = 0; i < SIZE; i++ ){
			for ( int j = 0; j< SIZE; j++ ){
				if ( d.domain[i][j].size() != 1 ) solution = false;
			}
		}
		return solution;
	}
	
	//New Class Domains
	public class Domains {
	LinkedList<Integer>[][] domain = ( LinkedList<Integer>[][] ) Array.newInstance( LinkedList.class, SIZE, SIZE );
		//Create domain array
		public Domains(int[][] board) {
			for (int i = 0; i < SIZE; i++){
				for (int j = 0; j < SIZE; j++){
					if (board[i][j] == 0){
						domain[i][j] = new LinkedList<Integer>( Arrays.asList( 1, 2, 3, 4, 5, 6, 7, 8, 9 ));
					} else {
						domain[i][j] = new LinkedList<Integer>( Arrays.asList( board[i][j] ) );
					}
				}
			}
		}
		// Create domain array
		public Domains( Domains d ){
			for ( int i = 0; i < SIZE; i++ ){
				for ( int j = 0; j < SIZE; j++ ){
					this.domain[i][j] = new LinkedList<Integer>();
					for ( Integer current: d.domain[i][j] ) {
						this.domain[i][j].add( new Integer( current ) );
					}
				}
			}
		}
		
		//Transfer domain array into output array
		public int[][] output(){
			int[][] output = new int[SIZE][SIZE];
			for (int i = 0; i < SIZE; i++){
				for (int j = 0; j < SIZE; j++){
					output[i][j] = domain[i][j].getFirst();
				}
			}
			return output;
		}
	}
}
