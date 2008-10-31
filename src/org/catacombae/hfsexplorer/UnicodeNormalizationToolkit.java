/*-
 * Copyright (C) 2006-2007 Erik Larsson
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catacombae.hfsexplorer;

//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.PrintStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Map;
//import java.util.TreeMap;

/**
 * A class implementing a table for handling the decomposition of characters that's necessary when
 * creating Unicode filenames for an HFS+ file system.
 * Created from http://developer.apple.com/technotes/tn/tn1150table.html and verified against it.
 * Also including a Hangul decomposition algorithm from the Unicode Book.
 */
public class UnicodeNormalizationToolkit {
    private static final UnicodeNormalizationToolkit defaultInstance = new UnicodeNormalizationToolkit();
    
    private Map<Character, char[]> decompositionTable;
    private TrieNode compositionTrie;
    
    /** This class encapsulates code copied from http://unicode.org/reports/tr15/#Hangul in order
	to deal with Hangul decomposition algorithmically. No indication of any copyright issues.
	The same algorithm is presented in section 3.11 of the Unicode standard 3.0.*/
    private static class HangulDecomposition {
	static final int
	    SBase = 0xAC00, LBase = 0x1100, VBase = 0x1161, TBase = 0x11A7,
	    LCount = 19, VCount = 21, TCount = 28,
	    NCount = VCount * TCount,   // 588
	    SCount = LCount * NCount;   // 11172
	
	public static String decomposeHangul(char s) {
	    int SIndex = s - SBase;
	    if (SIndex < 0 || SIndex >= SCount) {
		return String.valueOf(s);
	    }
	    StringBuffer result = new StringBuffer();
	    int L = LBase + SIndex / NCount;
	    int V = VBase + (SIndex % NCount) / TCount;
	    int T = TBase + SIndex % TCount;
	    result.append((char)L);
	    result.append((char)V);
	    if (T != TBase) result.append((char)T);
	    return result.toString();
	}
	public static String composeHangul(String source) {
	    int len = source.length();
	    if (len == 0) return "";
	    StringBuffer result = new StringBuffer();
	    char last = source.charAt(0);            // copy first char
	    result.append(last);
	    
	    for (int i = 1; i < len; ++i) {
		char ch = source.charAt(i);
		
		// 1. check to see if two current characters are L and V
		
		int LIndex = last - LBase;
		if (0 <= LIndex && LIndex < LCount) {
		    int VIndex = ch - VBase;
		    if (0 <= VIndex && VIndex < VCount) {
			
			// make syllable of form LV
			
			last = (char)(SBase + (LIndex * VCount + VIndex) * TCount);
			
			result.setCharAt(result.length()-1, last); // reset last
			continue; // discard ch
		    }
		}
		
		
		// 2. check to see if two current characters are LV and T
		
		int SIndex = last - SBase;
		if (0 <= SIndex && SIndex < SCount && (SIndex % TCount) == 0) {
		    int TIndex = ch - TBase;
		    if (0 < TIndex && TIndex < TCount) {
			
			// make syllable of form LVT
			
			last += TIndex;
			result.setCharAt(result.length()-1, last); // reset last
			continue; // discard ch
		    }
		}
		// if neither case was true, just add the character
		last = ch;
		result.append(ch);
	    }
	    return result.toString();
	}
    }
    
    private static long nextID = 0;
    private static class TrieNode {
	private final Hashtable<Character, TrieNode> childNodes = new Hashtable<Character, TrieNode>();
	private char[] replacementSequence = null;
	private char trig;
	private long id;
	
	public TrieNode(char trig) { this.trig = trig; this.id = nextID++; }
	
	public void addChild(char nextChar, TrieNode childNode) {
	    childNodes.put(nextChar, childNode);
	}
	public TrieNode getChild(char nextChar) {
	    return childNodes.get(nextChar);
	}
	public Collection<TrieNode> getChildren() {
	    return childNodes.values();
	}
	public void setReplacementSequence(char[] seq) {
	    this.replacementSequence = seq;
	}
	public char[] getReplacementSequence() { return replacementSequence; }
        
        @Override
	public String toString() {
	    return "{" + id + "} 0x" + Util.toHexStringBE(trig) +
		(replacementSequence == null?"":(" -> 0x" + Util.toHexStringBE(replacementSequence)));
	}
    }

    private UnicodeNormalizationToolkit() {
	this(new HashMap<Character, char[]>());
    }
    /** This method can be used in order to tune which Map implementation is used (default is HashMap). */
    private UnicodeNormalizationToolkit(Map<Character, char[]> decompositionTable) {
	this.decompositionTable = decompositionTable;
	buildDecompositionTable(decompositionTable);
	this.compositionTrie = buildCompositionTrie(decompositionTable);
	//checkTrie(compositionTrie);
    }
    
    public static UnicodeNormalizationToolkit getDefaultInstance() {
	return defaultInstance;
    }
    public static UnicodeNormalizationToolkit getCustomInstance(Map<Character, char[]> decompositionTable) {
	return new UnicodeNormalizationToolkit(decompositionTable);
    }
    
    /**
     * Breaks down character <code>c</code> into one or more decomposed Unicode characters.
     */
    public char[] decompose(char c) {
	int codepoint = c & 0xFFFF;
	if(codepoint >= 0xAC00 && codepoint <= 0xD7A3) {
	    // We have a Hangul character
	    //throw new RuntimeException("Hangul decomposition not yet implemented.");
	    return HangulDecomposition.decomposeHangul(c).toCharArray();
	}
	else {
	    char[] subst = decompositionTable.get(c);
	    if(subst == null)
		subst = new char[] { c };
	    char[] res = new char[subst.length];
	    for(int i = 0; i < subst.length; ++i)
		res[i] = subst[i];
	    return res;
	}
    }
    
    public String compose(String decomposedString) {
	//System.err.println("compose");
	StringBuilder sb = new StringBuilder();
	LinkedList<TrieNode> matchSequence = new LinkedList<TrieNode>();
	for(int i = 0; i < decomposedString.length(); ++i) {
	    //System.err.println("i = " + i);
	    char[] replacementSequence = null;
	    matchSequence.clear();

	    /* First, we loop through the characters, starting at i, and matches characters in the
	       trie until no more match is found. We push each node in a stack for later processing. */
	    TrieNode tn = compositionTrie;
	    int charsRead = 0;
	    while(tn != null && i+charsRead < decomposedString.length()) {
		char current = decomposedString.charAt(i+charsRead);
		tn = tn.getChild(current);
		if(tn != null) {
		    matchSequence.addFirst(tn);
		    ++charsRead;
		}
		//System.err.print(" -> 0x" + Util.toHexStringBE(nextChar));
	    }
	    //System.err.println(" <BREAK>");
	    
	    /* To find the longest matching substring, we must loop from the back of the match
	       sequence and find the first (last) TrieNode with a replacement sequence. We have
	       conveniently arranged the match sequence in a LIFO manner, so we just have to loop. */
	    //System.err.print("  {read:" + charsRead + "}");
	    for(TrieNode cur : matchSequence) {
		//System.err.print(cur.toString() + " > "); 
		if(cur.getReplacementSequence() != null) {
		    //System.err.println("<REPLACEMENT FOUND: " + Util.toHexStringBE(cur.getReplacementSequence()) + ">{read:" + charsRead + "}");
		    replacementSequence = cur.getReplacementSequence();
		    break;
		}
		else
		    --charsRead;
	    }
//  	    if(replacementSequence == null)
//  		System.err.println("<NOTHING FOUND>{read:" + charsRead + "}");
	    

	    if(replacementSequence != null) {
		sb.append(replacementSequence);
		i += charsRead - 1;
	    }
	    else
		sb.append(decomposedString.charAt(i));
	}
	return HangulDecomposition.composeHangul(sb.toString());
    }

    public Map<Character, char[]> getDecompositionTable() {
	return decompositionTable;
    }
    
    /** Throws a RuntimeException if the trie contains non-leaf nodes with replacement sequences. */
    private void checkTrie(TrieNode root) {
	checkTrie(root, "  ");
    }
    private void checkTrie(TrieNode root, String prefix) {
	Collection<TrieNode> children = root.getChildren();
	//if(children.size() > 0 && root.getReplacementSequence() != null)
	    //throw new RuntimeException("Inconsistent trie.");
	if(root.getReplacementSequence() != null) {
	    System.err.print(prefix + root.toString());// + " -> 0x" + Util.toHexStringBE(root.getReplacementSequence()));
	    if(children.size() > 0)
		System.err.println(" <INCONSISTENCY!>");
	    else
		System.err.println();
	}
	else
	    System.err.println(prefix + root.toString());
	
	for(TrieNode tn : children)
	    checkTrie(tn, prefix + "  ");
    }
    
    /**
     * Test main method that takes an output file as args[0], and prints the decomposition
     * table to that file, in the same form as doc/decomposition_ref.txt (a cut and paste
     * text file from the table at http://developer.apple.com/technotes/tn/tn1150table.html).
     * Use org.catacombae.hfsexplorer.testcode.LineCompare to compare the generated text
     * file to decomposition_ref.txt (ignores empty lines and different line endings).
     */
    /*
    public static void main(String[] args) throws IOException {
	// Useage of TreeMap is essential to produce a sorted output.
	UnicodeNormalizationToolkit ud = new UnicodeNormalizationToolkit(new TreeMap<Character, char[]>());
	PrintStream out = new PrintStream(new FileOutputStream(args[0]), true, "US-ASCII");
	for(Map.Entry<Character, char[]> cur : ud.decompositionTable.entrySet()) {
	    out.println("0x" + Util.toHexStringBE((short)cur.getKey().charValue()).toUpperCase());
	    char[] subst = cur.getValue();
	    out.print("0x" + Util.toHexStringBE((short)subst[0]).toUpperCase());
	    for(int i = 1; i < subst.length; ++i) {
		out.print(" 0x" + Util.toHexStringBE((short)subst[i]).toUpperCase());
	    }
	    out.println();
	}
    }
    */

    private static TrieNode buildCompositionTrie(Map<Character, char[]> decompositionTable) {
	final TrieNode rootNode = new TrieNode('\0');
	for(Map.Entry<Character, char[]> entry : decompositionTable.entrySet()) {
	    char key = entry.getKey();
	    char[] value = entry.getValue();
	    
	    TrieNode currentNode = rootNode;
	    for(char c : value) {
		TrieNode nextNode = currentNode.getChild(c);
		if(nextNode == null) {
		    nextNode = new TrieNode(c);
		    currentNode.addChild(c, nextNode);
		}
		currentNode = nextNode;
	    }
	    currentNode.setReplacementSequence(new char[] { key });
	}
	return rootNode;
    }
    
    private static void buildDecompositionTable(Map<Character, char[]> decompositionTable) {
	char key;
	char[] subst;
    
	key = (char)0x00C0;
	subst = new char[] { (char)0x0041, (char)0x0300 };
	decompositionTable.put(key, subst);
	

	key = (char)0x00C1;
	

	subst = new char[] { (char)0x0041, (char)0x0301 };
	decompositionTable.put(key, subst);

	

	key = (char)0x00C2;
	

	subst = new char[] { (char)0x0041, (char)0x0302 };
	decompositionTable.put(key, subst);
	

	key = (char)0x00C3;
	

	subst = new char[] { (char)0x0041, (char)0x0303 };
	decompositionTable.put(key, subst);

	key = (char)0x00C4;
	

	subst = new char[] { (char)0x0041, (char)0x0308 };
	decompositionTable.put(key, subst);
	

	key = (char)0x00C5;
	

	subst = new char[] { (char)0x0041, (char)0x030A };
	decompositionTable.put(key, subst);
	

	key = (char)0x00C7;
	

	subst = new char[] { (char)0x0043, (char)0x0327 };
	decompositionTable.put(key, subst);
	

	key = (char)0x00C8;
	

	subst = new char[] { (char)0x0045, (char)0x0300 };
	decompositionTable.put(key, subst);

	key = (char)0x00C9;
	

	subst = new char[] { (char)0x0045, (char)0x0301 };
	decompositionTable.put(key, subst);
	

	key = (char)0x00CA;
	

	subst = new char[] { (char)0x0045, (char)0x0302 };
	decompositionTable.put(key, subst);
	

	key = (char)0x00CB;
	

	subst = new char[] { (char)0x0045, (char)0x0308 };
	decompositionTable.put(key, subst);
	

	key = (char)0x00CC;
	

	subst = new char[] { (char)0x0049, (char)0x0300 };
	decompositionTable.put(key, subst);

	key = (char)0x00CD;
	

	subst = new char[] { (char)0x0049, (char)0x0301 };
	decompositionTable.put(key, subst);
	

	key = (char)0x00CE;
	

	subst = new char[] { (char)0x0049, (char)0x0302 };
	decompositionTable.put(key, subst);
	

	key = (char)0x00CF;
	

	subst = new char[] { (char)0x0049, (char)0x0308 };
	decompositionTable.put(key, subst);
	

	key = (char)0x00D1;
	

	subst = new char[] { (char)0x004E, (char)0x0303 };
	decompositionTable.put(key, subst);

	key = (char)0x00D2;
	

	subst = new char[] { (char)0x004F, (char)0x0300 };
	decompositionTable.put(key, subst);
	

	key = (char)0x00D3;
	

	subst = new char[] { (char)0x004F, (char)0x0301 };
	decompositionTable.put(key, subst);
	

	key = (char)0x00D4;
	

	subst = new char[] { (char)0x004F, (char)0x0302 };
	decompositionTable.put(key, subst);
	

	key = (char)0x00D5;
	

	subst = new char[] { (char)0x004F, (char)0x0303 };
	decompositionTable.put(key, subst);

	key = (char)0x00D6;
	

	subst = new char[] { (char)0x004F, (char)0x0308 };
	decompositionTable.put(key, subst);
	

	key = (char)0x00D9;
	

	subst = new char[] { (char)0x0055, (char)0x0300 };
	decompositionTable.put(key, subst);
	

	key = (char)0x00DA;
	

	subst = new char[] { (char)0x0055, (char)0x0301 };
	decompositionTable.put(key, subst);
	

	key = (char)0x00DB;
	

	subst = new char[] { (char)0x0055, (char)0x0302 };
	decompositionTable.put(key, subst);

	key = (char)0x00DC;
	

	subst = new char[] { (char)0x0055, (char)0x0308 };
	decompositionTable.put(key, subst);
	

	key = (char)0x00DD;
	

	subst = new char[] { (char)0x0059, (char)0x0301 };
	decompositionTable.put(key, subst);
	

	key = (char)0x00E0;
	

	subst = new char[] { (char)0x0061, (char)0x0300 };
	decompositionTable.put(key, subst);
	

	key = (char)0x00E1;
	

	subst = new char[] { (char)0x0061, (char)0x0301 };
	decompositionTable.put(key, subst);

	key = (char)0x00E2;
	

	subst = new char[] { (char)0x0061, (char)0x0302 };
	decompositionTable.put(key, subst);
	

	key = (char)0x00E3;
	

	subst = new char[] { (char)0x0061, (char)0x0303 };
	decompositionTable.put(key, subst);
	

	key = (char)0x00E4;
	

	subst = new char[] { (char)0x0061, (char)0x0308 };
	decompositionTable.put(key, subst);
	

	key = (char)0x00E5;
	

	subst = new char[] { (char)0x0061, (char)0x030A };
	decompositionTable.put(key, subst);

	key = (char)0x00E7;
	

	subst = new char[] { (char)0x0063, (char)0x0327 };
	decompositionTable.put(key, subst);
	

	key = (char)0x00E8;
	

	subst = new char[] { (char)0x0065, (char)0x0300 };
	decompositionTable.put(key, subst);
	

	key = (char)0x00E9;
	

	subst = new char[] { (char)0x0065, (char)0x0301 };
	decompositionTable.put(key, subst);
	

	key = (char)0x00EA;
	

	subst = new char[] { (char)0x0065, (char)0x0302 };
	decompositionTable.put(key, subst);

	key = (char)0x00EB;
	

	subst = new char[] { (char)0x0065, (char)0x0308 };
	decompositionTable.put(key, subst);
	

	key = (char)0x00EC;
	

	subst = new char[] { (char)0x0069, (char)0x0300 };
	decompositionTable.put(key, subst);
	

	key = (char)0x00ED;
	

	subst = new char[] { (char)0x0069, (char)0x0301 };
	decompositionTable.put(key, subst);
	

	key = (char)0x00EE;
	

	subst = new char[] { (char)0x0069, (char)0x0302 };
	decompositionTable.put(key, subst);

	key = (char)0x00EF;
	

	subst = new char[] { (char)0x0069, (char)0x0308 };
	decompositionTable.put(key, subst);
	

	key = (char)0x00F1;
	

	subst = new char[] { (char)0x006E, (char)0x0303 };
	decompositionTable.put(key, subst);
	

	key = (char)0x00F2;
	

	subst = new char[] { (char)0x006F, (char)0x0300 };
	decompositionTable.put(key, subst);
	

	key = (char)0x00F3;
	

	subst = new char[] { (char)0x006F, (char)0x0301 };
	decompositionTable.put(key, subst);

	key = (char)0x00F4;
	

	subst = new char[] { (char)0x006F, (char)0x0302 };
	decompositionTable.put(key, subst);
	  	  	  	  	  	 

	key = (char)0x00F5;
	

	subst = new char[] { (char)0x006F, (char)0x0303 };
	decompositionTable.put(key, subst);
	

	key = (char)0x00F6;
	

	subst = new char[] { (char)0x006F, (char)0x0308 };
	decompositionTable.put(key, subst);
	

	key = (char)0x00F9;
	

	subst = new char[] { (char)0x0075, (char)0x0300 };
	decompositionTable.put(key, subst);
	

	key = (char)0x00FA;
	

	subst = new char[] { (char)0x0075, (char)0x0301 };
	decompositionTable.put(key, subst);

	key = (char)0x00FB;
	

	subst = new char[] { (char)0x0075, (char)0x0302 };
	decompositionTable.put(key, subst);
	

	key = (char)0x00FC;
	

	subst = new char[] { (char)0x0075, (char)0x0308 };
	decompositionTable.put(key, subst);
	

	key = (char)0x00FD;
	

	subst = new char[] { (char)0x0079, (char)0x0301 };
	decompositionTable.put(key, subst);
	

	key = (char)0x00FF;
	

	subst = new char[] { (char)0x0079, (char)0x0308 };
	decompositionTable.put(key, subst);

	key = (char)0x0100;
	

	subst = new char[] { (char)0x0041, (char)0x0304 };
	decompositionTable.put(key, subst);
	

	key = (char)0x0101;
	

	subst = new char[] { (char)0x0061, (char)0x0304 };
	decompositionTable.put(key, subst);
	

	key = (char)0x0102;
	

	subst = new char[] { (char)0x0041, (char)0x0306 };
	decompositionTable.put(key, subst);
	

	key = (char)0x0103;
	

	subst = new char[] { (char)0x0061, (char)0x0306 };
	decompositionTable.put(key, subst);

	key = (char)0x0104;
	

	subst = new char[] { (char)0x0041, (char)0x0328 };
	decompositionTable.put(key, subst);
	

	key = (char)0x0105;
	

	subst = new char[] { (char)0x0061, (char)0x0328 };
	decompositionTable.put(key, subst);
	

	key = (char)0x0106;
	

	subst = new char[] { (char)0x0043, (char)0x0301 };
	decompositionTable.put(key, subst);
	

	key = (char)0x0107;
	

	subst = new char[] { (char)0x0063, (char)0x0301 };
	decompositionTable.put(key, subst);

	key = (char)0x0108;
	

	subst = new char[] { (char)0x0043, (char)0x0302 };
	decompositionTable.put(key, subst);
	

	key = (char)0x0109;
	

	subst = new char[] { (char)0x0063, (char)0x0302 };
	decompositionTable.put(key, subst);
	

	key = (char)0x010A;
	

	subst = new char[] { (char)0x0043, (char)0x0307 };
	decompositionTable.put(key, subst);
	

	key = (char)0x010B;
	

	subst = new char[] { (char)0x0063, (char)0x0307 };
	decompositionTable.put(key, subst);

	key = (char)0x010C;
	

	subst = new char[] { (char)0x0043, (char)0x030C };
	decompositionTable.put(key, subst);
	

	key = (char)0x010D;
	

	subst = new char[] { (char)0x0063, (char)0x030C };
	decompositionTable.put(key, subst);
	

	key = (char)0x010E;
	

	subst = new char[] { (char)0x0044, (char)0x030C };
	decompositionTable.put(key, subst);
	

	key = (char)0x010F;
	

	subst = new char[] { (char)0x0064, (char)0x030C };
	decompositionTable.put(key, subst);

	key = (char)0x0112;
	

	subst = new char[] { (char)0x0045, (char)0x0304 };
	decompositionTable.put(key, subst);
	

	key = (char)0x0113;
	

	subst = new char[] { (char)0x0065, (char)0x0304 };
	decompositionTable.put(key, subst);
	

	key = (char)0x0114;
	

	subst = new char[] { (char)0x0045, (char)0x0306 };
	decompositionTable.put(key, subst);
	

	key = (char)0x0115;
	

	subst = new char[] { (char)0x0065, (char)0x0306 };
	decompositionTable.put(key, subst);

	key = (char)0x0116;
	

	subst = new char[] { (char)0x0045, (char)0x0307 };
	decompositionTable.put(key, subst);
	  	  	  	  	  	 

	key = (char)0x0117;
	

	subst = new char[] { (char)0x0065, (char)0x0307 };
	decompositionTable.put(key, subst);
	

	key = (char)0x0118;
	

	subst = new char[] { (char)0x0045, (char)0x0328 };
	decompositionTable.put(key, subst);
	

	key = (char)0x0119;
	

	subst = new char[] { (char)0x0065, (char)0x0328 };
	decompositionTable.put(key, subst);
	

	key = (char)0x011A;
	

	subst = new char[] { (char)0x0045, (char)0x030C };
	decompositionTable.put(key, subst);

	key = (char)0x011B;
	

	subst = new char[] { (char)0x0065, (char)0x030C };
	decompositionTable.put(key, subst);
	

	key = (char)0x011C;
	

	subst = new char[] { (char)0x0047, (char)0x0302 };
	decompositionTable.put(key, subst);
	

	key = (char)0x011D;
	

	subst = new char[] { (char)0x0067, (char)0x0302 };
	decompositionTable.put(key, subst);
	

	key = (char)0x011E;
	

	subst = new char[] { (char)0x0047, (char)0x0306 };
	decompositionTable.put(key, subst);

	key = (char)0x011F;
	

	subst = new char[] { (char)0x0067, (char)0x0306 };
	decompositionTable.put(key, subst);
	

	key = (char)0x0120;
	

	subst = new char[] { (char)0x0047, (char)0x0307 };
	decompositionTable.put(key, subst);
	

	key = (char)0x0121;
	

	subst = new char[] { (char)0x0067, (char)0x0307 };
	decompositionTable.put(key, subst);
	

	key = (char)0x0122;
	

	subst = new char[] { (char)0x0047, (char)0x0327 };
	decompositionTable.put(key, subst);

	key = (char)0x0123;
	

	subst = new char[] { (char)0x0067, (char)0x0327 };
	decompositionTable.put(key, subst);
	

	key = (char)0x0124;
	

	subst = new char[] { (char)0x0048, (char)0x0302 };
	decompositionTable.put(key, subst);
	

	key = (char)0x0125;
	

	subst = new char[] { (char)0x0068, (char)0x0302 };
	decompositionTable.put(key, subst);
	

	key = (char)0x0128;
	

	subst = new char[] { (char)0x0049, (char)0x0303 };
	decompositionTable.put(key, subst);

	key = (char)0x0129;
	

	subst = new char[] { (char)0x0069, (char)0x0303 };
	decompositionTable.put(key, subst);
	

	key = (char)0x012A;
	

	subst = new char[] { (char)0x0049, (char)0x0304 };
	decompositionTable.put(key, subst);
	

	key = (char)0x012B;
	

	subst = new char[] { (char)0x0069, (char)0x0304 };
	decompositionTable.put(key, subst);
	

	key = (char)0x012C;
	

	subst = new char[] { (char)0x0049, (char)0x0306 };
	decompositionTable.put(key, subst);

	key = (char)0x012D;
	

	subst = new char[] { (char)0x0069, (char)0x0306 };
	decompositionTable.put(key, subst);
	

	key = (char)0x012E;
	

	subst = new char[] { (char)0x0049, (char)0x0328 };
	decompositionTable.put(key, subst);
	

	key = (char)0x012F;
	

	subst = new char[] { (char)0x0069, (char)0x0328 };
	decompositionTable.put(key, subst);
	

	key = (char)0x0130;
	

	subst = new char[] { (char)0x0049, (char)0x0307 };
	decompositionTable.put(key, subst);

	key = (char)0x0134;
	

	subst = new char[] { (char)0x004A, (char)0x0302 };
	decompositionTable.put(key, subst);
	

	key = (char)0x0135;
	

	subst = new char[] { (char)0x006A, (char)0x0302 };
	decompositionTable.put(key, subst);
	

	key = (char)0x0136;
	

	subst = new char[] { (char)0x004B, (char)0x0327 };
	decompositionTable.put(key, subst);
	

	key = (char)0x0137;
	

	subst = new char[] { (char)0x006B, (char)0x0327 };
	decompositionTable.put(key, subst);

	key = (char)0x0139;
	

	subst = new char[] { (char)0x004C, (char)0x0301 };
	decompositionTable.put(key, subst);
	

	key = (char)0x013A;
	

	subst = new char[] { (char)0x006C, (char)0x0301 };
	decompositionTable.put(key, subst);
	

	key = (char)0x013B;
	

	subst = new char[] { (char)0x004C, (char)0x0327 };
	decompositionTable.put(key, subst);
	

	key = (char)0x013C;
	

	subst = new char[] { (char)0x006C, (char)0x0327 };
	decompositionTable.put(key, subst);

	key = (char)0x013D;
	

	subst = new char[] { (char)0x004C, (char)0x030C };
	decompositionTable.put(key, subst);
	

	key = (char)0x013E;
	

	subst = new char[] { (char)0x006C, (char)0x030C };
	decompositionTable.put(key, subst);
	

	key = (char)0x0143;
	

	subst = new char[] { (char)0x004E, (char)0x0301 };
	decompositionTable.put(key, subst);
	

	key = (char)0x0144;
	

	subst = new char[] { (char)0x006E, (char)0x0301 };
	decompositionTable.put(key, subst);

	key = (char)0x0145;
	

	subst = new char[] { (char)0x004E, (char)0x0327 };
	decompositionTable.put(key, subst);
	  	  	  	  	  	 

	key = (char)0x0146;
	

	subst = new char[] { (char)0x006E, (char)0x0327 };
	decompositionTable.put(key, subst);
	

	key = (char)0x0147;
	

	subst = new char[] { (char)0x004E, (char)0x030C };
	decompositionTable.put(key, subst);
	

	key = (char)0x0148;
	

	subst = new char[] { (char)0x006E, (char)0x030C };
	decompositionTable.put(key, subst);
	

	key = (char)0x014C;
	

	subst = new char[] { (char)0x004F, (char)0x0304 };
	decompositionTable.put(key, subst);

	key = (char)0x014D;
	

	subst = new char[] { (char)0x006F, (char)0x0304 };
	decompositionTable.put(key, subst);
	

	key = (char)0x014E;
	

	subst = new char[] { (char)0x004F, (char)0x0306 };
	decompositionTable.put(key, subst);
	

	key = (char)0x014F;
	

	subst = new char[] { (char)0x006F, (char)0x0306 };
	decompositionTable.put(key, subst);
	

	key = (char)0x0150;
	

	subst = new char[] { (char)0x004F, (char)0x030B };
	decompositionTable.put(key, subst);

	key = (char)0x0151;
	

	subst = new char[] { (char)0x006F, (char)0x030B };
	decompositionTable.put(key, subst);
	  	  	  	  	  	 

	key = (char)0x0154;
	

	subst = new char[] { (char)0x0052, (char)0x0301 };
	decompositionTable.put(key, subst);
	

	key = (char)0x0155;
	

	subst = new char[] { (char)0x0072, (char)0x0301 };
	decompositionTable.put(key, subst);
	

	key = (char)0x0156;
	

	subst = new char[] { (char)0x0052, (char)0x0327 };
	decompositionTable.put(key, subst);
	

	key = (char)0x0157;
	

	subst = new char[] { (char)0x0072, (char)0x0327 };
	decompositionTable.put(key, subst);

	key = (char)0x0158;
	

	subst = new char[] { (char)0x0052, (char)0x030C };
	decompositionTable.put(key, subst);
	

	key = (char)0x0159;
	

	subst = new char[] { (char)0x0072, (char)0x030C };
	decompositionTable.put(key, subst);
	

	key = (char)0x015A;
	

	subst = new char[] { (char)0x0053, (char)0x0301 };
	decompositionTable.put(key, subst);
	

	key = (char)0x015B;
	

	subst = new char[] { (char)0x0073, (char)0x0301 };
	decompositionTable.put(key, subst);

	key = (char)0x015C;
	

	subst = new char[] { (char)0x0053, (char)0x0302 };
	decompositionTable.put(key, subst);
	

	key = (char)0x015D;
	

	subst = new char[] { (char)0x0073, (char)0x0302 };
	decompositionTable.put(key, subst);
	

	key = (char)0x015E;
	

	subst = new char[] { (char)0x0053, (char)0x0327 };
	decompositionTable.put(key, subst);
	

	key = (char)0x015F;
	

	subst = new char[] { (char)0x0073, (char)0x0327 };
	decompositionTable.put(key, subst);

	key = (char)0x0160;
	

	subst = new char[] { (char)0x0053, (char)0x030C };
	decompositionTable.put(key, subst);
	

	key = (char)0x0161;
	

	subst = new char[] { (char)0x0073, (char)0x030C };
	decompositionTable.put(key, subst);
	

	key = (char)0x0162;
	

	subst = new char[] { (char)0x0054, (char)0x0327 };
	decompositionTable.put(key, subst);
	

	key = (char)0x0163;
	

	subst = new char[] { (char)0x0074, (char)0x0327 };
	decompositionTable.put(key, subst);

	key = (char)0x0164;
	

	subst = new char[] { (char)0x0054, (char)0x030C };
	decompositionTable.put(key, subst);
	

	key = (char)0x0165;
	

	subst = new char[] { (char)0x0074, (char)0x030C };
	decompositionTable.put(key, subst);
	

	key = (char)0x0168;
	

	subst = new char[] { (char)0x0055, (char)0x0303 };
	decompositionTable.put(key, subst);
	

	key = (char)0x0169;
	

	subst = new char[] { (char)0x0075, (char)0x0303 };
	decompositionTable.put(key, subst);

	key = (char)0x016A;
	

	subst = new char[] { (char)0x0055, (char)0x0304 };
	decompositionTable.put(key, subst);
	

	key = (char)0x016B;
	

	subst = new char[] { (char)0x0075, (char)0x0304 };
	decompositionTable.put(key, subst);
	

	key = (char)0x016C;
	

	subst = new char[] { (char)0x0055, (char)0x0306 };
	decompositionTable.put(key, subst);
	

	key = (char)0x016D;
	

	subst = new char[] { (char)0x0075, (char)0x0306 };
	decompositionTable.put(key, subst);

	key = (char)0x016E;
	

	subst = new char[] { (char)0x0055, (char)0x030A };
	decompositionTable.put(key, subst);
	

	key = (char)0x016F;
	

	subst = new char[] { (char)0x0075, (char)0x030A };
	decompositionTable.put(key, subst);
	

	key = (char)0x0170;
	

	subst = new char[] { (char)0x0055, (char)0x030B };
	decompositionTable.put(key, subst);
	

	key = (char)0x0171;
	

	subst = new char[] { (char)0x0075, (char)0x030B };
	decompositionTable.put(key, subst);

	key = (char)0x0172;
	

	subst = new char[] { (char)0x0055, (char)0x0328 };
	decompositionTable.put(key, subst);
	

	key = (char)0x0173;
	

	subst = new char[] { (char)0x0075, (char)0x0328 };
	decompositionTable.put(key, subst);
	

	key = (char)0x0174;
	

	subst = new char[] { (char)0x0057, (char)0x0302 };
	decompositionTable.put(key, subst);
	

	key = (char)0x0175;
	

	subst = new char[] { (char)0x0077, (char)0x0302 };
	decompositionTable.put(key, subst);

	key = (char)0x0176;
	

	subst = new char[] { (char)0x0059, (char)0x0302 };
	decompositionTable.put(key, subst);
	

	key = (char)0x0177;
	

	subst = new char[] { (char)0x0079, (char)0x0302 };
	decompositionTable.put(key, subst);
	

	key = (char)0x0178;
	

	subst = new char[] { (char)0x0059, (char)0x0308 };
	decompositionTable.put(key, subst);
	

	key = (char)0x0179;
	

	subst = new char[] { (char)0x005A, (char)0x0301 };
	decompositionTable.put(key, subst);

	key = (char)0x017A;
	

	subst = new char[] { (char)0x007A, (char)0x0301 };
	decompositionTable.put(key, subst);
	

	key = (char)0x017B;
	

	subst = new char[] { (char)0x005A, (char)0x0307 };
	decompositionTable.put(key, subst);
	

	key = (char)0x017C;
	

	subst = new char[] { (char)0x007A, (char)0x0307 };
	decompositionTable.put(key, subst);
	

	key = (char)0x017D;
	

	subst = new char[] { (char)0x005A, (char)0x030C };
	decompositionTable.put(key, subst);

	key = (char)0x017E;
	

	subst = new char[] { (char)0x007A, (char)0x030C };
	decompositionTable.put(key, subst);
	

	key = (char)0x01A0;
	

	subst = new char[] { (char)0x004F, (char)0x031B };
	decompositionTable.put(key, subst);
	

	key = (char)0x01A1;
	

	subst = new char[] { (char)0x006F, (char)0x031B };
	decompositionTable.put(key, subst);
	

	key = (char)0x01AF;
	

	subst = new char[] { (char)0x0055, (char)0x031B };
	decompositionTable.put(key, subst);

	key = (char)0x01B0;
	

	subst = new char[] { (char)0x0075, (char)0x031B };
	decompositionTable.put(key, subst);
	

	key = (char)0x01CD;
	

	subst = new char[] { (char)0x0041, (char)0x030C };
	decompositionTable.put(key, subst);
	

	key = (char)0x01CE;
	

	subst = new char[] { (char)0x0061, (char)0x030C };
	decompositionTable.put(key, subst);
	

	key = (char)0x01CF;
	

	subst = new char[] { (char)0x0049, (char)0x030C };
	decompositionTable.put(key, subst);

	key = (char)0x01D0;
	

	subst = new char[] { (char)0x0069, (char)0x030C };
	decompositionTable.put(key, subst);
	

	key = (char)0x01D1;
	

	subst = new char[] { (char)0x004F, (char)0x030C };
	decompositionTable.put(key, subst);
	

	key = (char)0x01D2;
	

	subst = new char[] { (char)0x006F, (char)0x030C };
	decompositionTable.put(key, subst);
	

	key = (char)0x01D3;
	

	subst = new char[] { (char)0x0055, (char)0x030C };
	decompositionTable.put(key, subst);

	key = (char)0x01D4;
	

	subst = new char[] { (char)0x0075, (char)0x030C };
	decompositionTable.put(key, subst);
	

	key = (char)0x01D5;
	

	subst = new char[] { (char)0x0055, (char)0x0308, (char)0x0304 };
	decompositionTable.put(key, subst);
	

	key = (char)0x01D6;
	

	subst = new char[] { (char)0x0075, (char)0x0308, (char)0x0304 };
	decompositionTable.put(key, subst);
	

	key = (char)0x01D7;
	

	subst = new char[] { (char)0x0055, (char)0x0308, (char)0x0301 };
	decompositionTable.put(key, subst);

	key = (char)0x01D8;
	

	subst = new char[] { (char)0x0075, (char)0x0308, (char)0x0301 };
	decompositionTable.put(key, subst);
	

	key = (char)0x01D9;
	

	subst = new char[] { (char)0x0055, (char)0x0308, (char)0x030C };
	decompositionTable.put(key, subst);
	

	key = (char)0x01DA;
	

	subst = new char[] { (char)0x0075, (char)0x0308, (char)0x030C };
	decompositionTable.put(key, subst);
	

	key = (char)0x01DB;
	

	subst = new char[] { (char)0x0055, (char)0x0308, (char)0x0300 };
	decompositionTable.put(key, subst);

	key = (char)0x01DC;
	

	subst = new char[] { (char)0x0075, (char)0x0308, (char)0x0300 };
	decompositionTable.put(key, subst);
	

	key = (char)0x01DE;
	

	subst = new char[] { (char)0x0041, (char)0x0308, (char)0x0304 };
	decompositionTable.put(key, subst);
	

	key = (char)0x01DF;
	

	subst = new char[] { (char)0x0061, (char)0x0308, (char)0x0304 };
	decompositionTable.put(key, subst);
	

	key = (char)0x01E0;
	

	subst = new char[] { (char)0x0041, (char)0x0307, (char)0x0304 };
	decompositionTable.put(key, subst);

	key = (char)0x01E1;
	

	subst = new char[] { (char)0x0061, (char)0x0307, (char)0x0304 };
	decompositionTable.put(key, subst);
	

	key = (char)0x01E2;
	

	subst = new char[] { (char)0x00C6, (char)0x0304 };
	decompositionTable.put(key, subst);
	

	key = (char)0x01E3;
	

	subst = new char[] { (char)0x00E6, (char)0x0304 };
	decompositionTable.put(key, subst);
	

	key = (char)0x01E6;
	

	subst = new char[] { (char)0x0047, (char)0x030C };
	decompositionTable.put(key, subst);

	key = (char)0x01E7;
	

	subst = new char[] { (char)0x0067, (char)0x030C };
	decompositionTable.put(key, subst);
	

	key = (char)0x01E8;
	

	subst = new char[] { (char)0x004B, (char)0x030C };
	decompositionTable.put(key, subst);
	

	key = (char)0x01E9;
	

	subst = new char[] { (char)0x006B, (char)0x030C };
	decompositionTable.put(key, subst);
	

	key = (char)0x01EA;
	

	subst = new char[] { (char)0x004F, (char)0x0328 };
	decompositionTable.put(key, subst);

	key = (char)0x01EB;
	

	subst = new char[] { (char)0x006F, (char)0x0328 };
	decompositionTable.put(key, subst);
	

	key = (char)0x01EC;
	

	subst = new char[] { (char)0x004F, (char)0x0328, (char)0x0304 };
	decompositionTable.put(key, subst);
	

	key = (char)0x01ED;
	

	subst = new char[] { (char)0x006F, (char)0x0328, (char)0x0304 };
	decompositionTable.put(key, subst);
	

	key = (char)0x01EE;
	

	subst = new char[] { (char)0x01B7, (char)0x030C };
	decompositionTable.put(key, subst);

	key = (char)0x01EF;
	

	subst = new char[] { (char)0x0292, (char)0x030C };
	decompositionTable.put(key, subst);
	

	key = (char)0x01F0;
	

	subst = new char[] { (char)0x006A, (char)0x030C };
	decompositionTable.put(key, subst);
	

	key = (char)0x01F4;
	

	subst = new char[] { (char)0x0047, (char)0x0301 };
	decompositionTable.put(key, subst);
	

	key = (char)0x01F5;
	

	subst = new char[] { (char)0x0067, (char)0x0301 };
	decompositionTable.put(key, subst);

	key = (char)0x01FA;
	

	subst = new char[] { (char)0x0041, (char)0x030A, (char)0x0301 };
	decompositionTable.put(key, subst);
	

	key = (char)0x01FB;
	

	subst = new char[] { (char)0x0061, (char)0x030A, (char)0x0301 };
	decompositionTable.put(key, subst);
	

	key = (char)0x01FC;
	

	subst = new char[] { (char)0x00C6, (char)0x0301 };
	decompositionTable.put(key, subst);
	

	key = (char)0x01FD;
	

	subst = new char[] { (char)0x00E6, (char)0x0301 };
	decompositionTable.put(key, subst);

	key = (char)0x01FE;
	

	subst = new char[] { (char)0x00D8, (char)0x0301 };
	decompositionTable.put(key, subst);
	

	key = (char)0x01FF;
	

	subst = new char[] { (char)0x00F8, (char)0x0301 };
	decompositionTable.put(key, subst);
	

	key = (char)0x0200;
	

	subst = new char[] { (char)0x0041, (char)0x030F };
	decompositionTable.put(key, subst);
	

	key = (char)0x0201;
	

	subst = new char[] { (char)0x0061, (char)0x030F };
	decompositionTable.put(key, subst);

	key = (char)0x0202;
	

	subst = new char[] { (char)0x0041, (char)0x0311 };
	decompositionTable.put(key, subst);
	

	key = (char)0x0203;
	

	subst = new char[] { (char)0x0061, (char)0x0311 };
	decompositionTable.put(key, subst);
	

	key = (char)0x0204;
	

	subst = new char[] { (char)0x0045, (char)0x030F };
	decompositionTable.put(key, subst);
	

	key = (char)0x0205;
	

	subst = new char[] { (char)0x0065, (char)0x030F };
	decompositionTable.put(key, subst);

	key = (char)0x0206;
	

	subst = new char[] { (char)0x0045, (char)0x0311 };
	decompositionTable.put(key, subst);
	

	key = (char)0x0207;
	

	subst = new char[] { (char)0x0065, (char)0x0311 };
	decompositionTable.put(key, subst);
	

	key = (char)0x0208;
	

	subst = new char[] { (char)0x0049, (char)0x030F };
	decompositionTable.put(key, subst);
	

	key = (char)0x0209;
	

	subst = new char[] { (char)0x0069, (char)0x030F };
	decompositionTable.put(key, subst);

	key = (char)0x020A;
	

	subst = new char[] { (char)0x0049, (char)0x0311 };
	decompositionTable.put(key, subst);
	

	key = (char)0x020B;
	

	subst = new char[] { (char)0x0069, (char)0x0311 };
	decompositionTable.put(key, subst);
	

	key = (char)0x020C;
	

	subst = new char[] { (char)0x004F, (char)0x030F };
	decompositionTable.put(key, subst);
	

	key = (char)0x020D;
	

	subst = new char[] { (char)0x006F, (char)0x030F };
	decompositionTable.put(key, subst);

	key = (char)0x020E;
	

	subst = new char[] { (char)0x004F, (char)0x0311 };
	decompositionTable.put(key, subst);
	

	key = (char)0x020F;
	

	subst = new char[] { (char)0x006F, (char)0x0311 };
	decompositionTable.put(key, subst);
	

	key = (char)0x0210;
	

	subst = new char[] { (char)0x0052, (char)0x030F };
	decompositionTable.put(key, subst);
	

	key = (char)0x0211;
	

	subst = new char[] { (char)0x0072, (char)0x030F };
	decompositionTable.put(key, subst);

	key = (char)0x0212;
	

	subst = new char[] { (char)0x0052, (char)0x0311 };
	decompositionTable.put(key, subst);
	

	key = (char)0x0213;
	

	subst = new char[] { (char)0x0072, (char)0x0311 };
	decompositionTable.put(key, subst);
	

	key = (char)0x0214;
	

	subst = new char[] { (char)0x0055, (char)0x030F };
	decompositionTable.put(key, subst);
	

	key = (char)0x0215;
	

	subst = new char[] { (char)0x0075, (char)0x030F };
	decompositionTable.put(key, subst);

	key = (char)0x0216;
	

	subst = new char[] { (char)0x0055, (char)0x0311 };
	decompositionTable.put(key, subst);
	

	key = (char)0x0217;
	

	subst = new char[] { (char)0x0075, (char)0x0311 };
	decompositionTable.put(key, subst);
	

	key = (char)0x0310;
	

	subst = new char[] { (char)0x0306, (char)0x0307 };
	decompositionTable.put(key, subst);
	

	key = (char)0x0340;
	

	subst = new char[] { (char)0x0300 };
	decompositionTable.put(key, subst);
	
	key = (char)0x0341;
	

	subst = new char[] { (char)0x0301 };
	decompositionTable.put(key, subst);

	key = (char)0x0343;
	

	subst = new char[] { (char)0x0313 };
	decompositionTable.put(key, subst);

	key = (char)0x0344;
	

	subst = new char[] { (char)0x0308, (char)0x030D };
	decompositionTable.put(key, subst);
	

	key = (char)0x0374;
	

	subst = new char[] { (char)0x02B9 };
	decompositionTable.put(key, subst);

	key = (char)0x037E;
	

	subst = new char[] { (char)0x003B };
	decompositionTable.put(key, subst);

	key = (char)0x0385;
	

	subst = new char[] { (char)0x00A8, (char)0x030D };
	decompositionTable.put(key, subst);
	

	key = (char)0x0386;
	

	subst = new char[] { (char)0x0391, (char)0x030D };
	decompositionTable.put(key, subst);
	

	key = (char)0x0387;
	

	subst = new char[] { (char)0x00B7 };
	decompositionTable.put(key, subst);

	key = (char)0x0388;
	

	subst = new char[] { (char)0x0395, (char)0x030D };
	decompositionTable.put(key, subst);
	

	key = (char)0x0389;
	

	subst = new char[] { (char)0x0397, (char)0x030D };
	decompositionTable.put(key, subst);
	

	key = (char)0x038A;
	

	subst = new char[] { (char)0x0399, (char)0x030D };
	decompositionTable.put(key, subst);
	

	key = (char)0x038C;
	

	subst = new char[] { (char)0x039F, (char)0x030D };
	decompositionTable.put(key, subst);

	key = (char)0x038E;
	

	subst = new char[] { (char)0x03A5, (char)0x030D };
	decompositionTable.put(key, subst);
	

	key = (char)0x038F;
	

	subst = new char[] { (char)0x03A9, (char)0x030D };
	decompositionTable.put(key, subst);
	

	key = (char)0x0390;
	

	subst = new char[] { (char)0x03B9, (char)0x0308, (char)0x030D };
	decompositionTable.put(key, subst);
	

	key = (char)0x03AA;
	

	subst = new char[] { (char)0x0399, (char)0x0308 };
	decompositionTable.put(key, subst);

	key = (char)0x03AB;
	

	subst = new char[] { (char)0x03A5, (char)0x0308 };
	decompositionTable.put(key, subst);
	

	key = (char)0x03AC;
	

	subst = new char[] { (char)0x03B1, (char)0x030D };
	decompositionTable.put(key, subst);
	

	key = (char)0x03AD;
	

	subst = new char[] { (char)0x03B5, (char)0x030D };
	decompositionTable.put(key, subst);
	

	key = (char)0x03AE;
	

	subst = new char[] { (char)0x03B7, (char)0x030D };
	decompositionTable.put(key, subst);

	key = (char)0x03AF;
	

	subst = new char[] { (char)0x03B9, (char)0x030D };
	decompositionTable.put(key, subst);
	

	key = (char)0x03B0;
	

	subst = new char[] { (char)0x03C5, (char)0x0308, (char)0x030D };
	decompositionTable.put(key, subst);
	

	key = (char)0x03CA;
	

	subst = new char[] { (char)0x03B9, (char)0x0308 };
	decompositionTable.put(key, subst);
	

	key = (char)0x03CB;
	

	subst = new char[] { (char)0x03C5, (char)0x0308 };
	decompositionTable.put(key, subst);

	key = (char)0x03CC;
	

	subst = new char[] { (char)0x03BF, (char)0x030D };
	decompositionTable.put(key, subst);
	

	key = (char)0x03CD;
	

	subst = new char[] { (char)0x03C5, (char)0x030D };
	decompositionTable.put(key, subst);
	

	key = (char)0x03CE;
	

	subst = new char[] { (char)0x03C9, (char)0x030D };
	decompositionTable.put(key, subst);
	

	key = (char)0x03D3;
	

	subst = new char[] { (char)0x03D2, (char)0x030D };
	decompositionTable.put(key, subst);

	key = (char)0x03D4;
	

	subst = new char[] { (char)0x03D2, (char)0x0308 };
	decompositionTable.put(key, subst);
	

	key = (char)0x0401;
	

	subst = new char[] { (char)0x0415, (char)0x0308 };
	decompositionTable.put(key, subst);
	

	key = (char)0x0403;
	

	subst = new char[] { (char)0x0413, (char)0x0301 };
	decompositionTable.put(key, subst);
	

	key = (char)0x0407;
	

	subst = new char[] { (char)0x0406, (char)0x0308 };
	decompositionTable.put(key, subst);

	key = (char)0x040C;
	

	subst = new char[] { (char)0x041A, (char)0x0301 };
	decompositionTable.put(key, subst);
	

	key = (char)0x040E;
	

	subst = new char[] { (char)0x0423, (char)0x0306 };
	decompositionTable.put(key, subst);
	

	key = (char)0x0419;
	

	subst = new char[] { (char)0x0418, (char)0x0306 };
	decompositionTable.put(key, subst);
	

	key = (char)0x0439;
	

	subst = new char[] { (char)0x0438, (char)0x0306 };
	decompositionTable.put(key, subst);

	key = (char)0x0451;
	

	subst = new char[] { (char)0x0435, (char)0x0308 };
	decompositionTable.put(key, subst);
	

	key = (char)0x0453;
	

	subst = new char[] { (char)0x0433, (char)0x0301 };
	decompositionTable.put(key, subst);
	

	key = (char)0x0457;
	

	subst = new char[] { (char)0x0456, (char)0x0308 };
	decompositionTable.put(key, subst);
	

	key = (char)0x045C;
	

	subst = new char[] { (char)0x043A, (char)0x0301 };
	decompositionTable.put(key, subst);

	key = (char)0x045E;
	

	subst = new char[] { (char)0x0443, (char)0x0306 };
	decompositionTable.put(key, subst);
	

	key = (char)0x0476;
	

	subst = new char[] { (char)0x0474, (char)0x030F };
	decompositionTable.put(key, subst);
	

	key = (char)0x0477;
	

	subst = new char[] { (char)0x0475, (char)0x030F };
	decompositionTable.put(key, subst);
	

	key = (char)0x04C1;
	

	subst = new char[] { (char)0x0416, (char)0x0306 };
	decompositionTable.put(key, subst);

	key = (char)0x04C2;
	

	subst = new char[] { (char)0x0436, (char)0x0306 };
	decompositionTable.put(key, subst);
	

	key = (char)0x04D0;
	

	subst = new char[] { (char)0x0410, (char)0x0306 };
	decompositionTable.put(key, subst);
	

	key = (char)0x04D1;
	

	subst = new char[] { (char)0x0430, (char)0x0306 };
	decompositionTable.put(key, subst);
	

	key = (char)0x04D2;
	

	subst = new char[] { (char)0x0410, (char)0x0308 };
	decompositionTable.put(key, subst);

	key = (char)0x04D3;
	

	subst = new char[] { (char)0x0430, (char)0x0308 };
	decompositionTable.put(key, subst);
	

	key = (char)0x04D4;
	

	subst = new char[] { (char)0x00C6 };
	decompositionTable.put(key, subst);

	key = (char)0x04D5;
	

	subst = new char[] { (char)0x00E6 };
	decompositionTable.put(key, subst);

	key = (char)0x04D6;
	

	subst = new char[] { (char)0x0415, (char)0x0306 };
	decompositionTable.put(key, subst);

	key = (char)0x04D7;
	

	subst = new char[] { (char)0x0435, (char)0x0306 };
	decompositionTable.put(key, subst);
	

	key = (char)0x04D8;
	

	subst = new char[] { (char)0x018F };
	decompositionTable.put(key, subst);

	key = (char)0x04D9;
	

	subst = new char[] { (char)0x0259 };
	decompositionTable.put(key, subst);

	key = (char)0x04DA;
	

	subst = new char[] { (char)0x018F, (char)0x0308 };
	decompositionTable.put(key, subst);

	key = (char)0x04DB;
	

	subst = new char[] { (char)0x0259, (char)0x0308 };
	decompositionTable.put(key, subst);
	

	key = (char)0x04DC;
	

	subst = new char[] { (char)0x0416, (char)0x0308 };
	decompositionTable.put(key, subst);
	

	key = (char)0x04DD;
	

	subst = new char[] { (char)0x0436, (char)0x0308 };
	decompositionTable.put(key, subst);
	

	key = (char)0x04DE;
	

	subst = new char[] { (char)0x0417, (char)0x0308 };
	decompositionTable.put(key, subst);

	key = (char)0x04DF;
	

	subst = new char[] { (char)0x0437, (char)0x0308 };
	decompositionTable.put(key, subst);
	

	key = (char)0x04E0;
	

	subst = new char[] { (char)0x01B7 };
	decompositionTable.put(key, subst);

	key = (char)0x04E1;
	

	subst = new char[] { (char)0x0292 };
	decompositionTable.put(key, subst);

	key = (char)0x04E2;
	

	subst = new char[] { (char)0x0418, (char)0x0304 };
	decompositionTable.put(key, subst);

	key = (char)0x04E3;
	

	subst = new char[] { (char)0x0438, (char)0x0304 };
	decompositionTable.put(key, subst);
	

	key = (char)0x04E4;
	

	subst = new char[] { (char)0x0418, (char)0x0308 };
	decompositionTable.put(key, subst);
	

	key = (char)0x04E5;
	

	subst = new char[] { (char)0x0438, (char)0x0308 };
	decompositionTable.put(key, subst);
	

	key = (char)0x04E6;
	

	subst = new char[] { (char)0x041E, (char)0x0308 };
	decompositionTable.put(key, subst);

	key = (char)0x04E7;
	

	subst = new char[] { (char)0x043E, (char)0x0308 };
	decompositionTable.put(key, subst);
	

	key = (char)0x04E8;
	

	subst = new char[] { (char)0x019F };
	decompositionTable.put(key, subst);

	key = (char)0x04E9;
	

	subst = new char[] { (char)0x0275 };
	decompositionTable.put(key, subst);

	key = (char)0x04EA;
	

	subst = new char[] { (char)0x019F, (char)0x0308 };
	decompositionTable.put(key, subst);

	key = (char)0x04EB;
	

	subst = new char[] { (char)0x0275, (char)0x0308 };
	decompositionTable.put(key, subst);
	

	key = (char)0x04EE;
	

	subst = new char[] { (char)0x0423, (char)0x0304 };
	decompositionTable.put(key, subst);
	

	key = (char)0x04EF;
	

	subst = new char[] { (char)0x0443, (char)0x0304 };
	decompositionTable.put(key, subst);
	

	key = (char)0x04F0;
	

	subst = new char[] { (char)0x0423, (char)0x0308 };
	decompositionTable.put(key, subst);

	key = (char)0x04F1;
	

	subst = new char[] { (char)0x0443, (char)0x0308 };
	decompositionTable.put(key, subst);
	

	key = (char)0x04F2;
	

	subst = new char[] { (char)0x0423, (char)0x030B };
	decompositionTable.put(key, subst);
	

	key = (char)0x04F3;
	

	subst = new char[] { (char)0x0443, (char)0x030B };
	decompositionTable.put(key, subst);
	

	key = (char)0x04F4;
	

	subst = new char[] { (char)0x0427, (char)0x0308 };
	decompositionTable.put(key, subst);

	key = (char)0x04F5;
	

	subst = new char[] { (char)0x0447, (char)0x0308 };
	decompositionTable.put(key, subst);
	

	key = (char)0x04F8;
	

	subst = new char[] { (char)0x042B, (char)0x0308 };
	decompositionTable.put(key, subst);
	

	key = (char)0x04F9;
	

	subst = new char[] { (char)0x044B, (char)0x0308 };
	decompositionTable.put(key, subst);
	

	key = (char)0x0929;
	

	subst = new char[] { (char)0x0928, (char)0x093C };
	decompositionTable.put(key, subst);

	key = (char)0x0931;
	

	subst = new char[] { (char)0x0930, (char)0x093C };
	decompositionTable.put(key, subst);
	

	key = (char)0x0934;
	

	subst = new char[] { (char)0x0933, (char)0x093C };
	decompositionTable.put(key, subst);
	

	key = (char)0x0958;
	

	subst = new char[] { (char)0x0915, (char)0x093C };
	decompositionTable.put(key, subst);
	

	key = (char)0x0959;
	

	subst = new char[] { (char)0x0916, (char)0x093C };
	decompositionTable.put(key, subst);

	key = (char)0x095A;
	

	subst = new char[] { (char)0x0917, (char)0x093C };
	decompositionTable.put(key, subst);
	

	key = (char)0x095B;
	

	subst = new char[] { (char)0x091C, (char)0x093C };
	decompositionTable.put(key, subst);
	

	key = (char)0x095C;
	

	subst = new char[] { (char)0x0921, (char)0x093C };
	decompositionTable.put(key, subst);
	

	key = (char)0x095D;
	

	subst = new char[] { (char)0x0922, (char)0x093C };
	decompositionTable.put(key, subst);

	key = (char)0x095E;
	

	subst = new char[] { (char)0x092B, (char)0x093C };
	decompositionTable.put(key, subst);
	

	key = (char)0x095F;
	

	subst = new char[] { (char)0x092F, (char)0x093C };
	decompositionTable.put(key, subst);
	

	key = (char)0x09B0;
	

	subst = new char[] { (char)0x09AC, (char)0x09BC };
	decompositionTable.put(key, subst);
	

	key = (char)0x09CB;
	

	subst = new char[] { (char)0x09C7, (char)0x09BE };
	decompositionTable.put(key, subst);

	key = (char)0x09CC;
	

	subst = new char[] { (char)0x09C7, (char)0x09D7 };
	decompositionTable.put(key, subst);
	

	key = (char)0x09DC;
	

	subst = new char[] { (char)0x09A1, (char)0x09BC };
	decompositionTable.put(key, subst);
	

	key = (char)0x09DD;
	

	subst = new char[] { (char)0x09A2, (char)0x09BC };
	decompositionTable.put(key, subst);
	

	key = (char)0x09DF;
	

	subst = new char[] { (char)0x09AF, (char)0x09BC };
	decompositionTable.put(key, subst);

	key = (char)0x0A59;
	

	subst = new char[] { (char)0x0A16, (char)0x0A3C };
	decompositionTable.put(key, subst);
	

	key = (char)0x0A5A;
	

	subst = new char[] { (char)0x0A17, (char)0x0A3C };
	decompositionTable.put(key, subst);
	

	key = (char)0x0A5B;
	

	subst = new char[] { (char)0x0A1C, (char)0x0A3C };
	decompositionTable.put(key, subst);
	

	key = (char)0x0A5C;
	

	subst = new char[] { (char)0x0A21, (char)0x0A3C };
	decompositionTable.put(key, subst);

	key = (char)0x0A5E;
	

	subst = new char[] { (char)0x0A2B, (char)0x0A3C };
	decompositionTable.put(key, subst);
	

	key = (char)0x0B48;
	

	subst = new char[] { (char)0x0B47, (char)0x0B56 };
	decompositionTable.put(key, subst);
	

	key = (char)0x0B4B;
	

	subst = new char[] { (char)0x0B47, (char)0x0B3E };
	decompositionTable.put(key, subst);
	

	key = (char)0x0B4C;
	

	subst = new char[] { (char)0x0B47, (char)0x0B57 };
	decompositionTable.put(key, subst);

	key = (char)0x0B5C;
	

	subst = new char[] { (char)0x0B21, (char)0x0B3C };
	decompositionTable.put(key, subst);
	

	key = (char)0x0B5D;
	

	subst = new char[] { (char)0x0B22, (char)0x0B3C };
	decompositionTable.put(key, subst);
	

	key = (char)0x0B5F;
	

	subst = new char[] { (char)0x0B2F, (char)0x0B3C };
	decompositionTable.put(key, subst);
	

	key = (char)0x0B94;
	

	subst = new char[] { (char)0x0B92, (char)0x0BD7 };
	decompositionTable.put(key, subst);

	key = (char)0x0BCA;
	

	subst = new char[] { (char)0x0BC6, (char)0x0BBE };
	decompositionTable.put(key, subst);
	

	key = (char)0x0BCB;
	

	subst = new char[] { (char)0x0BC7, (char)0x0BBE };
	decompositionTable.put(key, subst);
	

	key = (char)0x0BCC;
	

	subst = new char[] { (char)0x0BC6, (char)0x0BD7 };
	decompositionTable.put(key, subst);
	

	key = (char)0x0C48;
	

	subst = new char[] { (char)0x0C46, (char)0x0C56 };
	decompositionTable.put(key, subst);

	key = (char)0x0CC0;
	

	subst = new char[] { (char)0x0CBF, (char)0x0CD5 };
	decompositionTable.put(key, subst);
	

	key = (char)0x0CC7;
	

	subst = new char[] { (char)0x0CC6, (char)0x0CD5 };
	decompositionTable.put(key, subst);
	

	key = (char)0x0CC8;
	

	subst = new char[] { (char)0x0CC6, (char)0x0CD6 };
	decompositionTable.put(key, subst);
	

	key = (char)0x0CCA;
	

	subst = new char[] { (char)0x0CC6, (char)0x0CC2 };
	decompositionTable.put(key, subst);

	key = (char)0x0CCB;
	

	subst = new char[] { (char)0x0CC6, (char)0x0CC2, (char)0x0CD5 };
	decompositionTable.put(key, subst);
	

	key = (char)0x0D4A;
	

	subst = new char[] { (char)0x0D46, (char)0x0D3E };
	decompositionTable.put(key, subst);
	

	key = (char)0x0D4B;
	

	subst = new char[] { (char)0x0D47, (char)0x0D3E };
	decompositionTable.put(key, subst);
	

	key = (char)0x0D4C;
	

	subst = new char[] { (char)0x0D46, (char)0x0D57 };
	decompositionTable.put(key, subst);

	key = (char)0x0E33;
	

	subst = new char[] { (char)0x0E4D, (char)0x0E32 };
	decompositionTable.put(key, subst);
	  	  	  	  	  	 

	key = (char)0x0EB3;
	

	subst = new char[] { (char)0x0ECD, (char)0x0EB2 };
	decompositionTable.put(key, subst);
	

	key = (char)0x0F43;
	

	subst = new char[] { (char)0x0F42, (char)0x0FB7 };
	decompositionTable.put(key, subst);
	

	key = (char)0x0F4D;
	

	subst = new char[] { (char)0x0F4C, (char)0x0FB7 };
	decompositionTable.put(key, subst);
	

	key = (char)0x0F52;
	

	subst = new char[] { (char)0x0F51, (char)0x0FB7 };
	decompositionTable.put(key, subst);

	key = (char)0x0F57;
	

	subst = new char[] { (char)0x0F56, (char)0x0FB7 };
	decompositionTable.put(key, subst);
	

	key = (char)0x0F5C;
	

	subst = new char[] { (char)0x0F5B, (char)0x0FB7 };
	decompositionTable.put(key, subst);
	

	key = (char)0x0F69;
	

	subst = new char[] { (char)0x0F40, (char)0x0FB5 };
	decompositionTable.put(key, subst);
	

	key = (char)0x0F73;
	

	subst = new char[] { (char)0x0F72, (char)0x0F71 };
	decompositionTable.put(key, subst);

	key = (char)0x0F75;
	

	subst = new char[] { (char)0x0F74, (char)0x0F71 };
	decompositionTable.put(key, subst);
	

	key = (char)0x0F76;
	

	subst = new char[] { (char)0x0FB2, (char)0x0F80 };
	decompositionTable.put(key, subst);
	

	key = (char)0x0F77;
	

	subst = new char[] { (char)0x0FB2, (char)0x0F80, (char)0x0F71 };
	decompositionTable.put(key, subst);
	

	key = (char)0x0F78;
	

	subst = new char[] { (char)0x0FB3, (char)0x0F80 };
	decompositionTable.put(key, subst);

	key = (char)0x0F79;
	

	subst = new char[] { (char)0x0FB3, (char)0x0F80, (char)0x0F71 };
	decompositionTable.put(key, subst);
	

	key = (char)0x0F81;
	

	subst = new char[] { (char)0x0F80, (char)0x0F71 };
	decompositionTable.put(key, subst);
	

	key = (char)0x0F93;
	

	subst = new char[] { (char)0x0F92, (char)0x0FB7 };
	decompositionTable.put(key, subst);
	

	key = (char)0x0F9D;
	

	subst = new char[] { (char)0x0F9C, (char)0x0FB7 };
	decompositionTable.put(key, subst);

	key = (char)0x0FA2;
	

	subst = new char[] { (char)0x0FA1, (char)0x0FB7 };
	decompositionTable.put(key, subst);
	

	key = (char)0x0FA7;
	

	subst = new char[] { (char)0x0FA6, (char)0x0FB7 };
	decompositionTable.put(key, subst);
	

	key = (char)0x0FAC;
	

	subst = new char[] { (char)0x0FAB, (char)0x0FB7 };
	decompositionTable.put(key, subst);
	

	key = (char)0x0FB9;
	

	subst = new char[] { (char)0x0F90, (char)0x0FB5 };
	decompositionTable.put(key, subst);

	key = (char)0x1E00;
	

	subst = new char[] { (char)0x0041, (char)0x0325 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E01;
	

	subst = new char[] { (char)0x0061, (char)0x0325 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E02;
	

	subst = new char[] { (char)0x0042, (char)0x0307 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E03;
	

	subst = new char[] { (char)0x0062, (char)0x0307 };
	decompositionTable.put(key, subst);

	key = (char)0x1E04;
	

	subst = new char[] { (char)0x0042, (char)0x0323 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E05;
	

	subst = new char[] { (char)0x0062, (char)0x0323 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E06;
	

	subst = new char[] { (char)0x0042, (char)0x0331 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E07;
	

	subst = new char[] { (char)0x0062, (char)0x0331 };
	decompositionTable.put(key, subst);

	key = (char)0x1E08;
	

	subst = new char[] { (char)0x0043, (char)0x0327, (char)0x0301 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E09;
	

	subst = new char[] { (char)0x0063, (char)0x0327, (char)0x0301 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E0A;
	

	subst = new char[] { (char)0x0044, (char)0x0307 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E0B;
	

	subst = new char[] { (char)0x0064, (char)0x0307 };
	decompositionTable.put(key, subst);

	key = (char)0x1E0C;
	

	subst = new char[] { (char)0x0044, (char)0x0323 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E0D;
	

	subst = new char[] { (char)0x0064, (char)0x0323 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E0E;
	

	subst = new char[] { (char)0x0044, (char)0x0331 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E0F;
	

	subst = new char[] { (char)0x0064, (char)0x0331 };
	decompositionTable.put(key, subst);

	key = (char)0x1E10;
	

	subst = new char[] { (char)0x0044, (char)0x0327 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E11;
	

	subst = new char[] { (char)0x0064, (char)0x0327 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E12;
	

	subst = new char[] { (char)0x0044, (char)0x032D };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E13;
	

	subst = new char[] { (char)0x0064, (char)0x032D };
	decompositionTable.put(key, subst);

	key = (char)0x1E14;
	

	subst = new char[] { (char)0x0045, (char)0x0304, (char)0x0300 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E15;
	

	subst = new char[] { (char)0x0065, (char)0x0304, (char)0x0300 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E16;
	

	subst = new char[] { (char)0x0045, (char)0x0304, (char)0x0301 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E17;
	

	subst = new char[] { (char)0x0065, (char)0x0304, (char)0x0301 };
	decompositionTable.put(key, subst);

	key = (char)0x1E18;
	

	subst = new char[] { (char)0x0045, (char)0x032D };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E19;
	

	subst = new char[] { (char)0x0065, (char)0x032D };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E1A;
	

	subst = new char[] { (char)0x0045, (char)0x0330 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E1B;
	

	subst = new char[] { (char)0x0065, (char)0x0330 };
	decompositionTable.put(key, subst);

	key = (char)0x1E1C;
	

	subst = new char[] { (char)0x0045, (char)0x0327, (char)0x0306 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E1D;
	

	subst = new char[] { (char)0x0065, (char)0x0327, (char)0x0306 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E1E;
	

	subst = new char[] { (char)0x0046, (char)0x0307 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E1F;
	

	subst = new char[] { (char)0x0066, (char)0x0307 };
	decompositionTable.put(key, subst);

	key = (char)0x1E20;
	

	subst = new char[] { (char)0x0047, (char)0x0304 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E21;
	

	subst = new char[] { (char)0x0067, (char)0x0304 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E22;
	

	subst = new char[] { (char)0x0048, (char)0x0307 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E23;
	

	subst = new char[] { (char)0x0068, (char)0x0307 };
	decompositionTable.put(key, subst);

	key = (char)0x1E24;
	

	subst = new char[] { (char)0x0048, (char)0x0323 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E25;
	

	subst = new char[] { (char)0x0068, (char)0x0323 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E26;
	

	subst = new char[] { (char)0x0048, (char)0x0308 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E27;
	

	subst = new char[] { (char)0x0068, (char)0x0308 };
	decompositionTable.put(key, subst);

	key = (char)0x1E28;
	

	subst = new char[] { (char)0x0048, (char)0x0327 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E29;
	

	subst = new char[] { (char)0x0068, (char)0x0327 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E2A;
	

	subst = new char[] { (char)0x0048, (char)0x032E };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E2B;
	

	subst = new char[] { (char)0x0068, (char)0x032E };
	decompositionTable.put(key, subst);

	key = (char)0x1E2C;
	

	subst = new char[] { (char)0x0049, (char)0x0330 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E2D;
	

	subst = new char[] { (char)0x0069, (char)0x0330 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E2E;
	

	subst = new char[] { (char)0x0049, (char)0x0308, (char)0x0301 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E2F;
	

	subst = new char[] { (char)0x0069, (char)0x0308, (char)0x0301 };
	decompositionTable.put(key, subst);

	key = (char)0x1E30;
	

	subst = new char[] { (char)0x004B, (char)0x0301 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E31;
	

	subst = new char[] { (char)0x006B, (char)0x0301 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E32;
	

	subst = new char[] { (char)0x004B, (char)0x0323 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E33;
	

	subst = new char[] { (char)0x006B, (char)0x0323 };
	decompositionTable.put(key, subst);

	key = (char)0x1E34;
	

	subst = new char[] { (char)0x004B, (char)0x0331 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E35;
	

	subst = new char[] { (char)0x006B, (char)0x0331 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E36;
	

	subst = new char[] { (char)0x004C, (char)0x0323 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E37;
	

	subst = new char[] { (char)0x006C, (char)0x0323 };
	decompositionTable.put(key, subst);

	key = (char)0x1E38;
	

	subst = new char[] { (char)0x004C, (char)0x0323, (char)0x0304 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E39;
	

	subst = new char[] { (char)0x006C, (char)0x0323, (char)0x0304 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E3A;
	

	subst = new char[] { (char)0x004C, (char)0x0331 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E3B;
	

	subst = new char[] { (char)0x006C, (char)0x0331 };
	decompositionTable.put(key, subst);

	key = (char)0x1E3C;
	

	subst = new char[] { (char)0x004C, (char)0x032D };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E3D;
	

	subst = new char[] { (char)0x006C, (char)0x032D };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E3E;
	

	subst = new char[] { (char)0x004D, (char)0x0301 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E3F;
	

	subst = new char[] { (char)0x006D, (char)0x0301 };
	decompositionTable.put(key, subst);

	key = (char)0x1E40;
	

	subst = new char[] { (char)0x004D, (char)0x0307 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E41;
	

	subst = new char[] { (char)0x006D, (char)0x0307 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E42;
	

	subst = new char[] { (char)0x004D, (char)0x0323 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E43;
	

	subst = new char[] { (char)0x006D, (char)0x0323 };
	decompositionTable.put(key, subst);

	key = (char)0x1E44;
	

	subst = new char[] { (char)0x004E, (char)0x0307 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E45;
	

	subst = new char[] { (char)0x006E, (char)0x0307 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E46;
	

	subst = new char[] { (char)0x004E, (char)0x0323 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E47;
	

	subst = new char[] { (char)0x006E, (char)0x0323 };
	decompositionTable.put(key, subst);

	key = (char)0x1E48;
	

	subst = new char[] { (char)0x004E, (char)0x0331 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E49;
	

	subst = new char[] { (char)0x006E, (char)0x0331 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E4A;
	

	subst = new char[] { (char)0x004E, (char)0x032D };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E4B;
	

	subst = new char[] { (char)0x006E, (char)0x032D };
	decompositionTable.put(key, subst);

	key = (char)0x1E4C;
	

	subst = new char[] { (char)0x004F, (char)0x0303, (char)0x0301 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E4D;
	

	subst = new char[] { (char)0x006F, (char)0x0303, (char)0x0301 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E4E;
	

	subst = new char[] { (char)0x004F, (char)0x0303, (char)0x0308 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E4F;
	

	subst = new char[] { (char)0x006F, (char)0x0303, (char)0x0308 };
	decompositionTable.put(key, subst);

	key = (char)0x1E50;
	

	subst = new char[] { (char)0x004F, (char)0x0304, (char)0x0300 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E51;
	

	subst = new char[] { (char)0x006F, (char)0x0304, (char)0x0300 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E52;
	

	subst = new char[] { (char)0x004F, (char)0x0304, (char)0x0301 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E53;
	

	subst = new char[] { (char)0x006F, (char)0x0304, (char)0x0301 };
	decompositionTable.put(key, subst);

	key = (char)0x1E54;
	

	subst = new char[] { (char)0x0050, (char)0x0301 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E55;
	

	subst = new char[] { (char)0x0070, (char)0x0301 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E56;
	

	subst = new char[] { (char)0x0050, (char)0x0307 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E57;
	

	subst = new char[] { (char)0x0070, (char)0x0307 };
	decompositionTable.put(key, subst);

	key = (char)0x1E58;
	

	subst = new char[] { (char)0x0052, (char)0x0307 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E59;
	

	subst = new char[] { (char)0x0072, (char)0x0307 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E5A;
	

	subst = new char[] { (char)0x0052, (char)0x0323 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E5B;
	

	subst = new char[] { (char)0x0072, (char)0x0323 };
	decompositionTable.put(key, subst);

	key = (char)0x1E5C;
	

	subst = new char[] { (char)0x0052, (char)0x0323, (char)0x0304 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E5D;
	

	subst = new char[] { (char)0x0072, (char)0x0323, (char)0x0304 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E5E;
	

	subst = new char[] { (char)0x0052, (char)0x0331 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E5F;
	

	subst = new char[] { (char)0x0072, (char)0x0331 };
	decompositionTable.put(key, subst);

	key = (char)0x1E60;
	

	subst = new char[] { (char)0x0053, (char)0x0307 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E61;
	

	subst = new char[] { (char)0x0073, (char)0x0307 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E62;
	

	subst = new char[] { (char)0x0053, (char)0x0323 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E63;
	

	subst = new char[] { (char)0x0073, (char)0x0323 };
	decompositionTable.put(key, subst);

	key = (char)0x1E64;
	

	subst = new char[] { (char)0x0053, (char)0x0301, (char)0x0307 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E65;
	

	subst = new char[] { (char)0x0073, (char)0x0301, (char)0x0307 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E66;
	

	subst = new char[] { (char)0x0053, (char)0x030C, (char)0x0307 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E67;
	

	subst = new char[] { (char)0x0073, (char)0x030C, (char)0x0307 };
	decompositionTable.put(key, subst);

	key = (char)0x1E68;
	

	subst = new char[] { (char)0x0053, (char)0x0323, (char)0x0307 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E69;
	

	subst = new char[] { (char)0x0073, (char)0x0323, (char)0x0307 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E6A;
	

	subst = new char[] { (char)0x0054, (char)0x0307 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E6B;
	

	subst = new char[] { (char)0x0074, (char)0x0307 };
	decompositionTable.put(key, subst);

	key = (char)0x1E6C;
	

	subst = new char[] { (char)0x0054, (char)0x0323 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E6D;
	

	subst = new char[] { (char)0x0074, (char)0x0323 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E6E;
	

	subst = new char[] { (char)0x0054, (char)0x0331 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E6F;
	

	subst = new char[] { (char)0x0074, (char)0x0331 };
	decompositionTable.put(key, subst);

	key = (char)0x1E70;
	

	subst = new char[] { (char)0x0054, (char)0x032D };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E71;
	

	subst = new char[] { (char)0x0074, (char)0x032D };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E72;
	

	subst = new char[] { (char)0x0055, (char)0x0324 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E73;
	

	subst = new char[] { (char)0x0075, (char)0x0324 };
	decompositionTable.put(key, subst);

	key = (char)0x1E74;
	

	subst = new char[] { (char)0x0055, (char)0x0330 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E75;
	

	subst = new char[] { (char)0x0075, (char)0x0330 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E76;
	

	subst = new char[] { (char)0x0055, (char)0x032D };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E77;
	

	subst = new char[] { (char)0x0075, (char)0x032D };
	decompositionTable.put(key, subst);

	key = (char)0x1E78;
	

	subst = new char[] { (char)0x0055, (char)0x0303, (char)0x0301 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E79;
	

	subst = new char[] { (char)0x0075, (char)0x0303, (char)0x0301 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E7A;
	

	subst = new char[] { (char)0x0055, (char)0x0304, (char)0x0308 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E7B;
	

	subst = new char[] { (char)0x0075, (char)0x0304, (char)0x0308 };
	decompositionTable.put(key, subst);

	key = (char)0x1E7C;
	

	subst = new char[] { (char)0x0056, (char)0x0303 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E7D;
	

	subst = new char[] { (char)0x0076, (char)0x0303 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E7E;
	

	subst = new char[] { (char)0x0056, (char)0x0323 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E7F;
	

	subst = new char[] { (char)0x0076, (char)0x0323 };
	decompositionTable.put(key, subst);

	key = (char)0x1E80;
	

	subst = new char[] { (char)0x0057, (char)0x0300 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E81;
	

	subst = new char[] { (char)0x0077, (char)0x0300 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E82;
	

	subst = new char[] { (char)0x0057, (char)0x0301 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E83;
	

	subst = new char[] { (char)0x0077, (char)0x0301 };
	decompositionTable.put(key, subst);

	key = (char)0x1E84;
	

	subst = new char[] { (char)0x0057, (char)0x0308 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E85;
	

	subst = new char[] { (char)0x0077, (char)0x0308 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E86;
	

	subst = new char[] { (char)0x0057, (char)0x0307 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E87;
	

	subst = new char[] { (char)0x0077, (char)0x0307 };
	decompositionTable.put(key, subst);

	key = (char)0x1E88;
	

	subst = new char[] { (char)0x0057, (char)0x0323 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E89;
	

	subst = new char[] { (char)0x0077, (char)0x0323 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E8A;
	

	subst = new char[] { (char)0x0058, (char)0x0307 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E8B;
	

	subst = new char[] { (char)0x0078, (char)0x0307 };
	decompositionTable.put(key, subst);

	key = (char)0x1E8C;
	

	subst = new char[] { (char)0x0058, (char)0x0308 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E8D;
	

	subst = new char[] { (char)0x0078, (char)0x0308 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E8E;
	

	subst = new char[] { (char)0x0059, (char)0x0307 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E8F;
	

	subst = new char[] { (char)0x0079, (char)0x0307 };
	decompositionTable.put(key, subst);

	key = (char)0x1E90;
	

	subst = new char[] { (char)0x005A, (char)0x0302 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E91;
	

	subst = new char[] { (char)0x007A, (char)0x0302 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E92;
	

	subst = new char[] { (char)0x005A, (char)0x0323 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E93;
	

	subst = new char[] { (char)0x007A, (char)0x0323 };
	decompositionTable.put(key, subst);

	key = (char)0x1E94;
	

	subst = new char[] { (char)0x005A, (char)0x0331 };
	decompositionTable.put(key, subst);
	  	  	  	  	  	 

	key = (char)0x1E95;
	

	subst = new char[] { (char)0x007A, (char)0x0331 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E96;
	

	subst = new char[] { (char)0x0068, (char)0x0331 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E97;
	

	subst = new char[] { (char)0x0074, (char)0x0308 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E98;
	

	subst = new char[] { (char)0x0077, (char)0x030A };
	decompositionTable.put(key, subst);

	key = (char)0x1E99;
	

	subst = new char[] { (char)0x0079, (char)0x030A };
	decompositionTable.put(key, subst);
	

	key = (char)0x1E9B;
	

	subst = new char[] { (char)0x017F, (char)0x0307 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1EA0;
	

	subst = new char[] { (char)0x0041, (char)0x0323 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1EA1;
	

	subst = new char[] { (char)0x0061, (char)0x0323 };
	decompositionTable.put(key, subst);

	key = (char)0x1EA2;
	

	subst = new char[] { (char)0x0041, (char)0x0309 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1EA3;
	

	subst = new char[] { (char)0x0061, (char)0x0309 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1EA4;
	

	subst = new char[] { (char)0x0041, (char)0x0302, (char)0x0301 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1EA5;
	

	subst = new char[] { (char)0x0061, (char)0x0302, (char)0x0301 };
	decompositionTable.put(key, subst);

	key = (char)0x1EA6;
	

	subst = new char[] { (char)0x0041, (char)0x0302, (char)0x0300 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1EA7;
	

	subst = new char[] { (char)0x0061, (char)0x0302, (char)0x0300 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1EA8;
	

	subst = new char[] { (char)0x0041, (char)0x0302, (char)0x0309 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1EA9;
	

	subst = new char[] { (char)0x0061, (char)0x0302, (char)0x0309 };
	decompositionTable.put(key, subst);

	key = (char)0x1EAA;
	

	subst = new char[] { (char)0x0041, (char)0x0302, (char)0x0303 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1EAB;
	

	subst = new char[] { (char)0x0061, (char)0x0302, (char)0x0303 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1EAC;
	

	subst = new char[] { (char)0x0041, (char)0x0323, (char)0x0302 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1EAD;
	

	subst = new char[] { (char)0x0061, (char)0x0323, (char)0x0302 };
	decompositionTable.put(key, subst);

	key = (char)0x1EAE;
	

	subst = new char[] { (char)0x0041, (char)0x0306, (char)0x0301 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1EAF;
	

	subst = new char[] { (char)0x0061, (char)0x0306, (char)0x0301 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1EB0;
	

	subst = new char[] { (char)0x0041, (char)0x0306, (char)0x0300 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1EB1;
	

	subst = new char[] { (char)0x0061, (char)0x0306, (char)0x0300 };
	decompositionTable.put(key, subst);

	key = (char)0x1EB2;
	

	subst = new char[] { (char)0x0041, (char)0x0306, (char)0x0309 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1EB3;
	

	subst = new char[] { (char)0x0061, (char)0x0306, (char)0x0309 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1EB4;
	

	subst = new char[] { (char)0x0041, (char)0x0306, (char)0x0303 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1EB5;
	

	subst = new char[] { (char)0x0061, (char)0x0306, (char)0x0303 };
	decompositionTable.put(key, subst);

	key = (char)0x1EB6;
	

	subst = new char[] { (char)0x0041, (char)0x0323, (char)0x0306 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1EB7;
	

	subst = new char[] { (char)0x0061, (char)0x0323, (char)0x0306 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1EB8;
	

	subst = new char[] { (char)0x0045, (char)0x0323 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1EB9;
	

	subst = new char[] { (char)0x0065, (char)0x0323 };
	decompositionTable.put(key, subst);

	key = (char)0x1EBA;
	

	subst = new char[] { (char)0x0045, (char)0x0309 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1EBB;
	

	subst = new char[] { (char)0x0065, (char)0x0309 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1EBC;
	

	subst = new char[] { (char)0x0045, (char)0x0303 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1EBD;
	

	subst = new char[] { (char)0x0065, (char)0x0303 };
	decompositionTable.put(key, subst);

	key = (char)0x1EBE;
	

	subst = new char[] { (char)0x0045, (char)0x0302, (char)0x0301 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1EBF;
	

	subst = new char[] { (char)0x0065, (char)0x0302, (char)0x0301 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1EC0;
	

	subst = new char[] { (char)0x0045, (char)0x0302, (char)0x0300 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1EC1;
	

	subst = new char[] { (char)0x0065, (char)0x0302, (char)0x0300 };
	decompositionTable.put(key, subst);

	key = (char)0x1EC2;
	

	subst = new char[] { (char)0x0045, (char)0x0302, (char)0x0309 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1EC3;
	

	subst = new char[] { (char)0x0065, (char)0x0302, (char)0x0309 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1EC4;
	

	subst = new char[] { (char)0x0045, (char)0x0302, (char)0x0303 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1EC5;
	

	subst = new char[] { (char)0x0065, (char)0x0302, (char)0x0303 };
	decompositionTable.put(key, subst);

	key = (char)0x1EC6;
	

	subst = new char[] { (char)0x0045, (char)0x0323, (char)0x0302 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1EC7;
	

	subst = new char[] { (char)0x0065, (char)0x0323, (char)0x0302 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1EC8;
	

	subst = new char[] { (char)0x0049, (char)0x0309 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1EC9;
	

	subst = new char[] { (char)0x0069, (char)0x0309 };
	decompositionTable.put(key, subst);

	key = (char)0x1ECA;
	

	subst = new char[] { (char)0x0049, (char)0x0323 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1ECB;
	

	subst = new char[] { (char)0x0069, (char)0x0323 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1ECC;
	

	subst = new char[] { (char)0x004F, (char)0x0323 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1ECD;
	

	subst = new char[] { (char)0x006F, (char)0x0323 };
	decompositionTable.put(key, subst);

	key = (char)0x1ECE;
	

	subst = new char[] { (char)0x004F, (char)0x0309 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1ECF;
	

	subst = new char[] { (char)0x006F, (char)0x0309 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1ED0;
	

	subst = new char[] { (char)0x004F, (char)0x0302, (char)0x0301 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1ED1;
	

	subst = new char[] { (char)0x006F, (char)0x0302, (char)0x0301 };
	decompositionTable.put(key, subst);

	key = (char)0x1ED2;
	

	subst = new char[] { (char)0x004F, (char)0x0302, (char)0x0300 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1ED3;
	

	subst = new char[] { (char)0x006F, (char)0x0302, (char)0x0300 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1ED4;
	

	subst = new char[] { (char)0x004F, (char)0x0302, (char)0x0309 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1ED5;
	

	subst = new char[] { (char)0x006F, (char)0x0302, (char)0x0309 };
	decompositionTable.put(key, subst);

	key = (char)0x1ED6;
	

	subst = new char[] { (char)0x004F, (char)0x0302, (char)0x0303 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1ED7;
	

	subst = new char[] { (char)0x006F, (char)0x0302, (char)0x0303 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1ED8;
	

	subst = new char[] { (char)0x004F, (char)0x0323, (char)0x0302 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1ED9;
	

	subst = new char[] { (char)0x006F, (char)0x0323, (char)0x0302 };
	decompositionTable.put(key, subst);

	key = (char)0x1EDA;
	

	subst = new char[] { (char)0x004F, (char)0x031B, (char)0x0301 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1EDB;
	

	subst = new char[] { (char)0x006F, (char)0x031B, (char)0x0301 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1EDC;
	

	subst = new char[] { (char)0x004F, (char)0x031B, (char)0x0300 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1EDD;
	

	subst = new char[] { (char)0x006F, (char)0x031B, (char)0x0300 };
	decompositionTable.put(key, subst);

	key = (char)0x1EDE;
	

	subst = new char[] { (char)0x004F, (char)0x031B, (char)0x0309 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1EDF;
	

	subst = new char[] { (char)0x006F, (char)0x031B, (char)0x0309 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1EE0;
	

	subst = new char[] { (char)0x004F, (char)0x031B, (char)0x0303 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1EE1;
	

	subst = new char[] { (char)0x006F, (char)0x031B, (char)0x0303 };
	decompositionTable.put(key, subst);

	key = (char)0x1EE2;
	

	subst = new char[] { (char)0x004F, (char)0x031B, (char)0x0323 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1EE3;
	

	subst = new char[] { (char)0x006F, (char)0x031B, (char)0x0323 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1EE4;
	

	subst = new char[] { (char)0x0055, (char)0x0323 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1EE5;
	

	subst = new char[] { (char)0x0075, (char)0x0323 };
	decompositionTable.put(key, subst);

	key = (char)0x1EE6;
	

	subst = new char[] { (char)0x0055, (char)0x0309 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1EE7;
	

	subst = new char[] { (char)0x0075, (char)0x0309 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1EE8;
	

	subst = new char[] { (char)0x0055, (char)0x031B, (char)0x0301 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1EE9;
	

	subst = new char[] { (char)0x0075, (char)0x031B, (char)0x0301 };
	decompositionTable.put(key, subst);

	key = (char)0x1EEA;
	

	subst = new char[] { (char)0x0055, (char)0x031B, (char)0x0300 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1EEB;
	

	subst = new char[] { (char)0x0075, (char)0x031B, (char)0x0300 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1EEC;
	

	subst = new char[] { (char)0x0055, (char)0x031B, (char)0x0309 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1EED;
	

	subst = new char[] { (char)0x0075, (char)0x031B, (char)0x0309 };
	decompositionTable.put(key, subst);

	key = (char)0x1EEE;
	

	subst = new char[] { (char)0x0055, (char)0x031B, (char)0x0303 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1EEF;
	

	subst = new char[] { (char)0x0075, (char)0x031B, (char)0x0303 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1EF0;
	

	subst = new char[] { (char)0x0055, (char)0x031B, (char)0x0323 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1EF1;
	

	subst = new char[] { (char)0x0075, (char)0x031B, (char)0x0323 };
	decompositionTable.put(key, subst);

	key = (char)0x1EF2;
	

	subst = new char[] { (char)0x0059, (char)0x0300 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1EF3;
	

	subst = new char[] { (char)0x0079, (char)0x0300 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1EF4;
	

	subst = new char[] { (char)0x0059, (char)0x0323 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1EF5;
	

	subst = new char[] { (char)0x0079, (char)0x0323 };
	decompositionTable.put(key, subst);

	key = (char)0x1EF6;
	

	subst = new char[] { (char)0x0059, (char)0x0309 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1EF7;
	

	subst = new char[] { (char)0x0079, (char)0x0309 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1EF8;
	

	subst = new char[] { (char)0x0059, (char)0x0303 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1EF9;
	

	subst = new char[] { (char)0x0079, (char)0x0303 };
	decompositionTable.put(key, subst);

	key = (char)0x1F00;
	

	subst = new char[] { (char)0x03B1, (char)0x0313 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F01;
	

	subst = new char[] { (char)0x03B1, (char)0x0314 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F02;
	

	subst = new char[] { (char)0x03B1, (char)0x0313, (char)0x0300 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F03;
	

	subst = new char[] { (char)0x03B1, (char)0x0314, (char)0x0300 };
	decompositionTable.put(key, subst);

	key = (char)0x1F04;
	

	subst = new char[] { (char)0x03B1, (char)0x0313, (char)0x0301 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F05;
	

	subst = new char[] { (char)0x03B1, (char)0x0314, (char)0x0301 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F06;
	

	subst = new char[] { (char)0x03B1, (char)0x0313, (char)0x0342 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F07;
	

	subst = new char[] { (char)0x03B1, (char)0x0314, (char)0x0342 };
	decompositionTable.put(key, subst);

	key = (char)0x1F08;
	

	subst = new char[] { (char)0x0391, (char)0x0313 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F09;
	

	subst = new char[] { (char)0x0391, (char)0x0314 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F0A;
	

	subst = new char[] { (char)0x0391, (char)0x0313, (char)0x0300 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F0B;
	

	subst = new char[] { (char)0x0391, (char)0x0314, (char)0x0300 };
	decompositionTable.put(key, subst);

	key = (char)0x1F0C;
	

	subst = new char[] { (char)0x0391, (char)0x0313, (char)0x0301 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F0D;
	

	subst = new char[] { (char)0x0391, (char)0x0314, (char)0x0301 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F0E;
	

	subst = new char[] { (char)0x0391, (char)0x0313, (char)0x0342 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F0F;
	

	subst = new char[] { (char)0x0391, (char)0x0314, (char)0x0342 };
	decompositionTable.put(key, subst);

	key = (char)0x1F10;
	

	subst = new char[] { (char)0x03B5, (char)0x0313 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F11;
	

	subst = new char[] { (char)0x03B5, (char)0x0314 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F12;
	

	subst = new char[] { (char)0x03B5, (char)0x0313, (char)0x0300 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F13;
	

	subst = new char[] { (char)0x03B5, (char)0x0314, (char)0x0300 };
	decompositionTable.put(key, subst);

	key = (char)0x1F14;
	

	subst = new char[] { (char)0x03B5, (char)0x0313, (char)0x0301 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F15;
	

	subst = new char[] { (char)0x03B5, (char)0x0314, (char)0x0301 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F18;
	

	subst = new char[] { (char)0x0395, (char)0x0313 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F19;
	

	subst = new char[] { (char)0x0395, (char)0x0314 };
	decompositionTable.put(key, subst);

	key = (char)0x1F1A;
	

	subst = new char[] { (char)0x0395, (char)0x0313, (char)0x0300 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F1B;
	

	subst = new char[] { (char)0x0395, (char)0x0314, (char)0x0300 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F1C;
	

	subst = new char[] { (char)0x0395, (char)0x0313, (char)0x0301 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F1D;
	

	subst = new char[] { (char)0x0395, (char)0x0314, (char)0x0301 };
	decompositionTable.put(key, subst);

	key = (char)0x1F20;
	

	subst = new char[] { (char)0x03B7, (char)0x0313 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F21;
	

	subst = new char[] { (char)0x03B7, (char)0x0314 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F22;
	

	subst = new char[] { (char)0x03B7, (char)0x0313, (char)0x0300 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F23;
	

	subst = new char[] { (char)0x03B7, (char)0x0314, (char)0x0300 };
	decompositionTable.put(key, subst);

	key = (char)0x1F24;
	

	subst = new char[] { (char)0x03B7, (char)0x0313, (char)0x0301 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F25;
	

	subst = new char[] { (char)0x03B7, (char)0x0314, (char)0x0301 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F26;
	

	subst = new char[] { (char)0x03B7, (char)0x0313, (char)0x0342 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F27;
	

	subst = new char[] { (char)0x03B7, (char)0x0314, (char)0x0342 };
	decompositionTable.put(key, subst);

	key = (char)0x1F28;
	

	subst = new char[] { (char)0x0397, (char)0x0313 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F29;
	

	subst = new char[] { (char)0x0397, (char)0x0314 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F2A;
	

	subst = new char[] { (char)0x0397, (char)0x0313, (char)0x0300 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F2B;
	

	subst = new char[] { (char)0x0397, (char)0x0314, (char)0x0300 };
	decompositionTable.put(key, subst);

	key = (char)0x1F2C;
	

	subst = new char[] { (char)0x0397, (char)0x0313, (char)0x0301 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F2D;
	

	subst = new char[] { (char)0x0397, (char)0x0314, (char)0x0301 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F2E;
	

	subst = new char[] { (char)0x0397, (char)0x0313, (char)0x0342 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F2F;
	

	subst = new char[] { (char)0x0397, (char)0x0314, (char)0x0342 };
	decompositionTable.put(key, subst);

	key = (char)0x1F30;
	

	subst = new char[] { (char)0x03B9, (char)0x0313 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F31;
	

	subst = new char[] { (char)0x03B9, (char)0x0314 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F32;
	

	subst = new char[] { (char)0x03B9, (char)0x0313, (char)0x0300 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F33;
	

	subst = new char[] { (char)0x03B9, (char)0x0314, (char)0x0300 };
	decompositionTable.put(key, subst);

	key = (char)0x1F34;
	

	subst = new char[] { (char)0x03B9, (char)0x0313, (char)0x0301 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F35;
	

	subst = new char[] { (char)0x03B9, (char)0x0314, (char)0x0301 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F36;
	

	subst = new char[] { (char)0x03B9, (char)0x0313, (char)0x0342 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F37;
	

	subst = new char[] { (char)0x03B9, (char)0x0314, (char)0x0342 };
	decompositionTable.put(key, subst);

	key = (char)0x1F38;
	

	subst = new char[] { (char)0x0399, (char)0x0313 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F39;
	

	subst = new char[] { (char)0x0399, (char)0x0314 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F3A;
	

	subst = new char[] { (char)0x0399, (char)0x0313, (char)0x0300 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F3B;
	

	subst = new char[] { (char)0x0399, (char)0x0314, (char)0x0300 };
	decompositionTable.put(key, subst);

	key = (char)0x1F3C;
	

	subst = new char[] { (char)0x0399, (char)0x0313, (char)0x0301 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F3D;
	

	subst = new char[] { (char)0x0399, (char)0x0314, (char)0x0301 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F3E;
	

	subst = new char[] { (char)0x0399, (char)0x0313, (char)0x0342 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F3F;
	

	subst = new char[] { (char)0x0399, (char)0x0314, (char)0x0342 };
	decompositionTable.put(key, subst);

	key = (char)0x1F40;
	

	subst = new char[] { (char)0x03BF, (char)0x0313 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F41;
	

	subst = new char[] { (char)0x03BF, (char)0x0314 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F42;
	

	subst = new char[] { (char)0x03BF, (char)0x0313, (char)0x0300 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F43;
	

	subst = new char[] { (char)0x03BF, (char)0x0314, (char)0x0300 };
	decompositionTable.put(key, subst);

	key = (char)0x1F44;
	

	subst = new char[] { (char)0x03BF, (char)0x0313, (char)0x0301 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F45;
	

	subst = new char[] { (char)0x03BF, (char)0x0314, (char)0x0301 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F48;
	

	subst = new char[] { (char)0x039F, (char)0x0313 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F49;
	

	subst = new char[] { (char)0x039F, (char)0x0314 };
	decompositionTable.put(key, subst);

	key = (char)0x1F4A;
	

	subst = new char[] { (char)0x039F, (char)0x0313, (char)0x0300 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F4B;
	

	subst = new char[] { (char)0x039F, (char)0x0314, (char)0x0300 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F4C;
	

	subst = new char[] { (char)0x039F, (char)0x0313, (char)0x0301 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F4D;
	

	subst = new char[] { (char)0x039F, (char)0x0314, (char)0x0301 };
	decompositionTable.put(key, subst);

	key = (char)0x1F50;
	

	subst = new char[] { (char)0x03C5, (char)0x0313 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F51;
	

	subst = new char[] { (char)0x03C5, (char)0x0314 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F52;
	

	subst = new char[] { (char)0x03C5, (char)0x0313, (char)0x0300 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F53;
	

	subst = new char[] { (char)0x03C5, (char)0x0314, (char)0x0300 };
	decompositionTable.put(key, subst);

	key = (char)0x1F54;
	

	subst = new char[] { (char)0x03C5, (char)0x0313, (char)0x0301 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F55;
	

	subst = new char[] { (char)0x03C5, (char)0x0314, (char)0x0301 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F56;
	

	subst = new char[] { (char)0x03C5, (char)0x0313, (char)0x0342 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F57;
	

	subst = new char[] { (char)0x03C5, (char)0x0314, (char)0x0342 };
	decompositionTable.put(key, subst);

	key = (char)0x1F59;
	

	subst = new char[] { (char)0x03A5, (char)0x0314 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F5B;
	

	subst = new char[] { (char)0x03A5, (char)0x0314, (char)0x0300 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F5D;
	

	subst = new char[] { (char)0x03A5, (char)0x0314, (char)0x0301 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F5F;
	

	subst = new char[] { (char)0x03A5, (char)0x0314, (char)0x0342 };
	decompositionTable.put(key, subst);

	key = (char)0x1F60;
	

	subst = new char[] { (char)0x03C9, (char)0x0313 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F61;
	

	subst = new char[] { (char)0x03C9, (char)0x0314 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F62;
	

	subst = new char[] { (char)0x03C9, (char)0x0313, (char)0x0300 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F63;
	

	subst = new char[] { (char)0x03C9, (char)0x0314, (char)0x0300 };
	decompositionTable.put(key, subst);

	key = (char)0x1F64;
	

	subst = new char[] { (char)0x03C9, (char)0x0313, (char)0x0301 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F65;
	

	subst = new char[] { (char)0x03C9, (char)0x0314, (char)0x0301 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F66;
	

	subst = new char[] { (char)0x03C9, (char)0x0313, (char)0x0342 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F67;
	

	subst = new char[] { (char)0x03C9, (char)0x0314, (char)0x0342 };
	decompositionTable.put(key, subst);

	key = (char)0x1F68;
	

	subst = new char[] { (char)0x03A9, (char)0x0313 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F69;
	

	subst = new char[] { (char)0x03A9, (char)0x0314 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F6A;
	

	subst = new char[] { (char)0x03A9, (char)0x0313, (char)0x0300 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F6B;
	

	subst = new char[] { (char)0x03A9, (char)0x0314, (char)0x0300 };
	decompositionTable.put(key, subst);

	key = (char)0x1F6C;
	

	subst = new char[] { (char)0x03A9, (char)0x0313, (char)0x0301 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F6D;
	

	subst = new char[] { (char)0x03A9, (char)0x0314, (char)0x0301 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F6E;
	

	subst = new char[] { (char)0x03A9, (char)0x0313, (char)0x0342 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F6F;
	

	subst = new char[] { (char)0x03A9, (char)0x0314, (char)0x0342 };
	decompositionTable.put(key, subst);

	key = (char)0x1F70;
	

	subst = new char[] { (char)0x03B1, (char)0x0300 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F71;
	

	subst = new char[] { (char)0x03B1, (char)0x0301 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F72;
	

	subst = new char[] { (char)0x03B5, (char)0x0300 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F73;
	

	subst = new char[] { (char)0x03B5, (char)0x0301 };
	decompositionTable.put(key, subst);

	key = (char)0x1F74;
	

	subst = new char[] { (char)0x03B7, (char)0x0300 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F75;
	

	subst = new char[] { (char)0x03B7, (char)0x0301 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F76;
	

	subst = new char[] { (char)0x03B9, (char)0x0300 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F77;
	

	subst = new char[] { (char)0x03B9, (char)0x0301 };
	decompositionTable.put(key, subst);

	key = (char)0x1F78;
	

	subst = new char[] { (char)0x03BF, (char)0x0300 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F79;
	

	subst = new char[] { (char)0x03BF, (char)0x0301 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F7A;
	

	subst = new char[] { (char)0x03C5, (char)0x0300 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F7B;
	

	subst = new char[] { (char)0x03C5, (char)0x0301 };
	decompositionTable.put(key, subst);

	key = (char)0x1F7C;
	

	subst = new char[] { (char)0x03C9, (char)0x0300 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F7D;
	

	subst = new char[] { (char)0x03C9, (char)0x0301 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F80;
	

	subst = new char[] { (char)0x03B1, (char)0x0345, (char)0x0313 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F81;
	

	subst = new char[] { (char)0x03B1, (char)0x0345, (char)0x0314 };
	decompositionTable.put(key, subst);

	key = (char)0x1F82;
	

	subst = new char[] { (char)0x03B1, (char)0x0345, (char)0x0313, (char)0x0300 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F83;
	

	subst = new char[] { (char)0x03B1, (char)0x0345, (char)0x0314, (char)0x0300 };
	decompositionTable.put(key, subst);
	  	  	  	 

	key = (char)0x1F84;
	

	subst = new char[] { (char)0x03B1, (char)0x0345, (char)0x0313, (char)0x0301 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F85;
	

	subst = new char[] { (char)0x03B1, (char)0x0345, (char)0x0314, (char)0x0301 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F86;
	

	subst = new char[] { (char)0x03B1, (char)0x0345, (char)0x0313, (char)0x0342 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F87;
	

	subst = new char[] { (char)0x03B1, (char)0x0345, (char)0x0314, (char)0x0342 };
	decompositionTable.put(key, subst);

	key = (char)0x1F88;
	

	subst = new char[] { (char)0x0391, (char)0x0345, (char)0x0313 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F89;
	

	subst = new char[] { (char)0x0391, (char)0x0345, (char)0x0314 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F8A;
	

	subst = new char[] { (char)0x0391, (char)0x0345, (char)0x0313, (char)0x0300 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F8B;
	

	subst = new char[] { (char)0x0391, (char)0x0345, (char)0x0314, (char)0x0300 };
	decompositionTable.put(key, subst);

	key = (char)0x1F8C;
	

	subst = new char[] { (char)0x0391, (char)0x0345, (char)0x0313, (char)0x0301 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F8D;
	

	subst = new char[] { (char)0x0391, (char)0x0345, (char)0x0314, (char)0x0301 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F8E;
	

	subst = new char[] { (char)0x0391, (char)0x0345, (char)0x0313, (char)0x0342 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F8F;
	

	subst = new char[] { (char)0x0391, (char)0x0345, (char)0x0314, (char)0x0342 };
	decompositionTable.put(key, subst);

	key = (char)0x1F90;
	

	subst = new char[] { (char)0x03B7, (char)0x0345, (char)0x0313 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F91;
	

	subst = new char[] { (char)0x03B7, (char)0x0345, (char)0x0314 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F92;
	

	subst = new char[] { (char)0x03B7, (char)0x0345, (char)0x0313, (char)0x0300 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F93;
	

	subst = new char[] { (char)0x03B7, (char)0x0345, (char)0x0314, (char)0x0300 };
	decompositionTable.put(key, subst);

	key = (char)0x1F94;
	

	subst = new char[] { (char)0x03B7, (char)0x0345, (char)0x0313, (char)0x0301 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F95;
	

	subst = new char[] { (char)0x03B7, (char)0x0345, (char)0x0314, (char)0x0301 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F96;
	

	subst = new char[] { (char)0x03B7, (char)0x0345, (char)0x0313, (char)0x0342 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F97;
	

	subst = new char[] { (char)0x03B7, (char)0x0345, (char)0x0314, (char)0x0342 };
	decompositionTable.put(key, subst);

	key = (char)0x1F98;
	

	subst = new char[] { (char)0x0397, (char)0x0345, (char)0x0313 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F99;
	

	subst = new char[] { (char)0x0397, (char)0x0345, (char)0x0314 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F9A;
	

	subst = new char[] { (char)0x0397, (char)0x0345, (char)0x0313, (char)0x0300 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F9B;
	

	subst = new char[] { (char)0x0397, (char)0x0345, (char)0x0314, (char)0x0300 };
	decompositionTable.put(key, subst);

	key = (char)0x1F9C;
	

	subst = new char[] { (char)0x0397, (char)0x0345, (char)0x0313, (char)0x0301 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F9D;
	

	subst = new char[] { (char)0x0397, (char)0x0345, (char)0x0314, (char)0x0301 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F9E;
	

	subst = new char[] { (char)0x0397, (char)0x0345, (char)0x0313, (char)0x0342 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1F9F;
	

	subst = new char[] { (char)0x0397, (char)0x0345, (char)0x0314, (char)0x0342 };
	decompositionTable.put(key, subst);

	key = (char)0x1FA0;
	

	subst = new char[] { (char)0x03C9, (char)0x0345, (char)0x0313 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1FA1;
	

	subst = new char[] { (char)0x03C9, (char)0x0345, (char)0x0314 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1FA2;
	

	subst = new char[] { (char)0x03C9, (char)0x0345, (char)0x0313, (char)0x0300 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1FA3;
	

	subst = new char[] { (char)0x03C9, (char)0x0345, (char)0x0314, (char)0x0300 };
	decompositionTable.put(key, subst);

	key = (char)0x1FA4;
	

	subst = new char[] { (char)0x03C9, (char)0x0345, (char)0x0313, (char)0x0301 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1FA5;
	

	subst = new char[] { (char)0x03C9, (char)0x0345, (char)0x0314, (char)0x0301 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1FA6;
	

	subst = new char[] { (char)0x03C9, (char)0x0345, (char)0x0313, (char)0x0342 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1FA7;
	

	subst = new char[] { (char)0x03C9, (char)0x0345, (char)0x0314, (char)0x0342 };
	decompositionTable.put(key, subst);

	key = (char)0x1FA8;
	

	subst = new char[] { (char)0x03A9, (char)0x0345, (char)0x0313 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1FA9;
	

	subst = new char[] { (char)0x03A9, (char)0x0345, (char)0x0314 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1FAA;
	

	subst = new char[] { (char)0x03A9, (char)0x0345, (char)0x0313, (char)0x0300 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1FAB;
	

	subst = new char[] { (char)0x03A9, (char)0x0345, (char)0x0314, (char)0x0300 };
	decompositionTable.put(key, subst);

	key = (char)0x1FAC;
	

	subst = new char[] { (char)0x03A9, (char)0x0345, (char)0x0313, (char)0x0301 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1FAD;
	

	subst = new char[] { (char)0x03A9, (char)0x0345, (char)0x0314, (char)0x0301 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1FAE;
	

	subst = new char[] { (char)0x03A9, (char)0x0345, (char)0x0313, (char)0x0342 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1FAF;
	

	subst = new char[] { (char)0x03A9, (char)0x0345, (char)0x0314, (char)0x0342 };
	decompositionTable.put(key, subst);

	key = (char)0x1FB0;
	

	subst = new char[] { (char)0x03B1, (char)0x0306 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1FB1;
	

	subst = new char[] { (char)0x03B1, (char)0x0304 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1FB2;
	

	subst = new char[] { (char)0x03B1, (char)0x0345, (char)0x0300 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1FB3;
	

	subst = new char[] { (char)0x03B1, (char)0x0345 };
	decompositionTable.put(key, subst);

	key = (char)0x1FB4;
	

	subst = new char[] { (char)0x03B1, (char)0x0345, (char)0x0301 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1FB6;
	

	subst = new char[] { (char)0x03B1, (char)0x0342 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1FB7;
	

	subst = new char[] { (char)0x03B1, (char)0x0345, (char)0x0342 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1FB8;
	

	subst = new char[] { (char)0x0391, (char)0x0306 };
	decompositionTable.put(key, subst);

	key = (char)0x1FB9;
	

	subst = new char[] { (char)0x0391, (char)0x0304 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1FBA;
	

	subst = new char[] { (char)0x0391, (char)0x0300 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1FBB;
	

	subst = new char[] { (char)0x0391, (char)0x0301 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1FBC;
	

	subst = new char[] { (char)0x0391, (char)0x0345 };
	decompositionTable.put(key, subst);

	key = (char)0x1FBE;
	

	subst = new char[] { (char)0x03B9 };
	decompositionTable.put(key, subst);

	key = (char)0x1FC1;
	

	subst = new char[] { (char)0x00A8, (char)0x0342 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1FC2;
	

	subst = new char[] { (char)0x03B7, (char)0x0345, (char)0x0300 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1FC3;
	

	subst = new char[] { (char)0x03B7, (char)0x0345 };
	decompositionTable.put(key, subst);

	key = (char)0x1FC4;
	

	subst = new char[] { (char)0x03B7, (char)0x0345, (char)0x0301 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1FC6;
	

	subst = new char[] { (char)0x03B7, (char)0x0342 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1FC7;
	

	subst = new char[] { (char)0x03B7, (char)0x0345, (char)0x0342 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1FC8;
	

	subst = new char[] { (char)0x0395, (char)0x0300 };
	decompositionTable.put(key, subst);

	key = (char)0x1FC9;
	

	subst = new char[] { (char)0x0395, (char)0x0301 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1FCA;
	

	subst = new char[] { (char)0x0397, (char)0x0300 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1FCB;
	

	subst = new char[] { (char)0x0397, (char)0x0301 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1FCC;
	

	subst = new char[] { (char)0x0397, (char)0x0345 };
	decompositionTable.put(key, subst);

	key = (char)0x1FCD;
	

	subst = new char[] { (char)0x1FBF, (char)0x0300 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1FCE;
	

	subst = new char[] { (char)0x1FBF, (char)0x0301 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1FCF;
	

	subst = new char[] { (char)0x1FBF, (char)0x0342 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1FD0;
	

	subst = new char[] { (char)0x03B9, (char)0x0306 };
	decompositionTable.put(key, subst);

	key = (char)0x1FD1;
	

	subst = new char[] { (char)0x03B9, (char)0x0304 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1FD2;
	

	subst = new char[] { (char)0x03B9, (char)0x0308, (char)0x0300 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1FD3;
	

	subst = new char[] { (char)0x03B9, (char)0x0308, (char)0x0301 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1FD6;
	

	subst = new char[] { (char)0x03B9, (char)0x0342 };
	decompositionTable.put(key, subst);

	key = (char)0x1FD7;
	

	subst = new char[] { (char)0x03B9, (char)0x0308, (char)0x0342 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1FD8;
	

	subst = new char[] { (char)0x0399, (char)0x0306 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1FD9;
	

	subst = new char[] { (char)0x0399, (char)0x0304 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1FDA;
	

	subst = new char[] { (char)0x0399, (char)0x0300 };
	decompositionTable.put(key, subst);

	key = (char)0x1FDB;
	

	subst = new char[] { (char)0x0399, (char)0x0301 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1FDD;
	

	subst = new char[] { (char)0x1FFE, (char)0x0300 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1FDE;
	

	subst = new char[] { (char)0x1FFE, (char)0x0301 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1FDF;
	

	subst = new char[] { (char)0x1FFE, (char)0x0342 };
	decompositionTable.put(key, subst);

	key = (char)0x1FE0;
	

	subst = new char[] { (char)0x03C5, (char)0x0306 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1FE1;
	

	subst = new char[] { (char)0x03C5, (char)0x0304 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1FE2;
	

	subst = new char[] { (char)0x03C5, (char)0x0308, (char)0x0300 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1FE3;
	

	subst = new char[] { (char)0x03C5, (char)0x0308, (char)0x0301 };
	decompositionTable.put(key, subst);

	key = (char)0x1FE4;
	

	subst = new char[] { (char)0x03C1, (char)0x0313 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1FE5;
	

	subst = new char[] { (char)0x03C1, (char)0x0314 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1FE6;
	

	subst = new char[] { (char)0x03C5, (char)0x0342 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1FE7;
	

	subst = new char[] { (char)0x03C5, (char)0x0308, (char)0x0342 };
	decompositionTable.put(key, subst);

	key = (char)0x1FE8;
	

	subst = new char[] { (char)0x03A5, (char)0x0306 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1FE9;
	

	subst = new char[] { (char)0x03A5, (char)0x0304 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1FEA;
	

	subst = new char[] { (char)0x03A5, (char)0x0300 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1FEB;
	

	subst = new char[] { (char)0x03A5, (char)0x0301 };
	decompositionTable.put(key, subst);

	key = (char)0x1FEC;
	

	subst = new char[] { (char)0x03A1, (char)0x0314 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1FED;
	

	subst = new char[] { (char)0x00A8, (char)0x0300 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1FEE;
	

	subst = new char[] { (char)0x00A8, (char)0x0301 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1FEF;
	

	subst = new char[] { (char)0x0060 };
	decompositionTable.put(key, subst);

	key = (char)0x1FF2;
	

	subst = new char[] { (char)0x03C9, (char)0x0345, (char)0x0300 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1FF3;
	

	subst = new char[] { (char)0x03C9, (char)0x0345 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1FF4;
	

	subst = new char[] { (char)0x03BF, (char)0x0345, (char)0x0301 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1FF6;
	

	subst = new char[] { (char)0x03C9, (char)0x0342 };
	decompositionTable.put(key, subst);

	key = (char)0x1FF7;
	

	subst = new char[] { (char)0x03C9, (char)0x0345, (char)0x0342 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1FF8;
	

	subst = new char[] { (char)0x039F, (char)0x0300 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1FF9;
	

	subst = new char[] { (char)0x039F, (char)0x0301 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1FFA;
	

	subst = new char[] { (char)0x03A9, (char)0x0300 };
	decompositionTable.put(key, subst);

	key = (char)0x1FFB;
	

	subst = new char[] { (char)0x03A9, (char)0x0301 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1FFC;
	

	subst = new char[] { (char)0x03A9, (char)0x0345 };
	decompositionTable.put(key, subst);
	

	key = (char)0x1FFD;
	

	subst = new char[] { (char)0x00B4 };
	decompositionTable.put(key, subst);

	key = (char)0x304C;
	

	subst = new char[] { (char)0x304B, (char)0x3099 };
	decompositionTable.put(key, subst);

	key = (char)0x304E;
	

	subst = new char[] { (char)0x304D, (char)0x3099 };
	decompositionTable.put(key, subst);
	

	key = (char)0x3050;
	

	subst = new char[] { (char)0x304F, (char)0x3099 };
	decompositionTable.put(key, subst);
	

	key = (char)0x3052;
	

	subst = new char[] { (char)0x3051, (char)0x3099 };
	decompositionTable.put(key, subst);
	

	key = (char)0x3054;
	

	subst = new char[] { (char)0x3053, (char)0x3099 };
	decompositionTable.put(key, subst);

	key = (char)0x3056;
	

	subst = new char[] { (char)0x3055, (char)0x3099 };
	decompositionTable.put(key, subst);
	

	key = (char)0x3058;
	

	subst = new char[] { (char)0x3057, (char)0x3099 };
	decompositionTable.put(key, subst);
	

	key = (char)0x305A;
	

	subst = new char[] { (char)0x3059, (char)0x3099 };
	decompositionTable.put(key, subst);
	

	key = (char)0x305C;
	

	subst = new char[] { (char)0x305B, (char)0x3099 };
	decompositionTable.put(key, subst);

	key = (char)0x305E;
	

	subst = new char[] { (char)0x305D, (char)0x3099 };
	decompositionTable.put(key, subst);
	

	key = (char)0x3060;
	

	subst = new char[] { (char)0x305F, (char)0x3099 };
	decompositionTable.put(key, subst);
	

	key = (char)0x3062;
	

	subst = new char[] { (char)0x3061, (char)0x3099 };
	decompositionTable.put(key, subst);
	

	key = (char)0x3065;
	

	subst = new char[] { (char)0x3064, (char)0x3099 };
	decompositionTable.put(key, subst);

	key = (char)0x3067;
	

	subst = new char[] { (char)0x3066, (char)0x3099 };
	decompositionTable.put(key, subst);
	

	key = (char)0x3069;
	

	subst = new char[] { (char)0x3068, (char)0x3099 };
	decompositionTable.put(key, subst);
	

	key = (char)0x3070;
	

	subst = new char[] { (char)0x306F, (char)0x3099 };
	decompositionTable.put(key, subst);
	

	key = (char)0x3071;
	

	subst = new char[] { (char)0x306F, (char)0x309A };
	decompositionTable.put(key, subst);

	key = (char)0x3073;
	

	subst = new char[] { (char)0x3072, (char)0x3099 };
	decompositionTable.put(key, subst);
	

	key = (char)0x3074;
	

	subst = new char[] { (char)0x3072, (char)0x309A };
	decompositionTable.put(key, subst);
	

	key = (char)0x3076;
	

	subst = new char[] { (char)0x3075, (char)0x3099 };
	decompositionTable.put(key, subst);
	

	key = (char)0x3077;
	

	subst = new char[] { (char)0x3075, (char)0x309A };
	decompositionTable.put(key, subst);

	key = (char)0x3079;
	

	subst = new char[] { (char)0x3078, (char)0x3099 };
	decompositionTable.put(key, subst);
	

	key = (char)0x307A;
	

	subst = new char[] { (char)0x3078, (char)0x309A };
	decompositionTable.put(key, subst);
	

	key = (char)0x307C;
	

	subst = new char[] { (char)0x307B, (char)0x3099 };
	decompositionTable.put(key, subst);
	

	key = (char)0x307D;
	

	subst = new char[] { (char)0x307B, (char)0x309A };
	decompositionTable.put(key, subst);

	key = (char)0x3094;
	

	subst = new char[] { (char)0x3046, (char)0x3099 };
	decompositionTable.put(key, subst);
	

	key = (char)0x309E;
	

	subst = new char[] { (char)0x309D, (char)0x3099 };
	decompositionTable.put(key, subst);
	

	key = (char)0x30AC;
	

	subst = new char[] { (char)0x30AB, (char)0x3099 };
	decompositionTable.put(key, subst);
	

	key = (char)0x30AE;
	

	subst = new char[] { (char)0x30AD, (char)0x3099 };
	decompositionTable.put(key, subst);

	key = (char)0x30B0;
	

	subst = new char[] { (char)0x30AF, (char)0x3099 };
	decompositionTable.put(key, subst);
	

	key = (char)0x30B2;
	

	subst = new char[] { (char)0x30B1, (char)0x3099 };
	decompositionTable.put(key, subst);
	

	key = (char)0x30B4;
	

	subst = new char[] { (char)0x30B3, (char)0x3099 };
	decompositionTable.put(key, subst);
	

	key = (char)0x30B6;
	

	subst = new char[] { (char)0x30B5, (char)0x3099 };
	decompositionTable.put(key, subst);

	key = (char)0x30B8;
	

	subst = new char[] { (char)0x30B7, (char)0x3099 };
	decompositionTable.put(key, subst);
	

	key = (char)0x30BA;
	

	subst = new char[] { (char)0x30B9, (char)0x3099 };
	decompositionTable.put(key, subst);
	

	key = (char)0x30BC;
	

	subst = new char[] { (char)0x30BB, (char)0x3099 };
	decompositionTable.put(key, subst);
	

	key = (char)0x30BE;
	

	subst = new char[] { (char)0x30BD, (char)0x3099 };
	decompositionTable.put(key, subst);

	key = (char)0x30C0;
	

	subst = new char[] { (char)0x30BF, (char)0x3099 };
	decompositionTable.put(key, subst);
	

	key = (char)0x30C2;
	

	subst = new char[] { (char)0x30C1, (char)0x3099 };
	decompositionTable.put(key, subst);
	

	key = (char)0x30C5;
	

	subst = new char[] { (char)0x30C4, (char)0x3099 };
	decompositionTable.put(key, subst);
	

	key = (char)0x30C7;
	

	subst = new char[] { (char)0x30C6, (char)0x3099 };
	decompositionTable.put(key, subst);

	key = (char)0x30C9;
	

	subst = new char[] { (char)0x30C8, (char)0x3099 };
	decompositionTable.put(key, subst);
	

	key = (char)0x30D0;
	

	subst = new char[] { (char)0x30CF, (char)0x3099 };
	decompositionTable.put(key, subst);
	

	key = (char)0x30D1;
	

	subst = new char[] { (char)0x30CF, (char)0x309A };
	decompositionTable.put(key, subst);
	

	key = (char)0x30D3;
	

	subst = new char[] { (char)0x30D2, (char)0x3099 };
	decompositionTable.put(key, subst);

	key = (char)0x30D4;
	

	subst = new char[] { (char)0x30D2, (char)0x309A };
	decompositionTable.put(key, subst);
	

	key = (char)0x30D6;
	

	subst = new char[] { (char)0x30D5, (char)0x3099 };
	decompositionTable.put(key, subst);
	

	key = (char)0x30D7;
	

	subst = new char[] { (char)0x30D5, (char)0x309A };
	decompositionTable.put(key, subst);
	

	key = (char)0x30D9;
	

	subst = new char[] { (char)0x30D8, (char)0x3099 };
	decompositionTable.put(key, subst);

	key = (char)0x30DA;
	

	subst = new char[] { (char)0x30D8, (char)0x309A };
	decompositionTable.put(key, subst);
	

	key = (char)0x30DC;
	

	subst = new char[] { (char)0x30DB, (char)0x3099 };
	decompositionTable.put(key, subst);
	

	key = (char)0x30DD;
	

	subst = new char[] { (char)0x30DB, (char)0x309A };
	decompositionTable.put(key, subst);
	

	key = (char)0x30F4;
	

	subst = new char[] { (char)0x30A6, (char)0x3099 };
	decompositionTable.put(key, subst);

	key = (char)0x30F7;
	

	subst = new char[] { (char)0x30EF, (char)0x3099 };
	decompositionTable.put(key, subst);
	

	key = (char)0x30F8;
	

	subst = new char[] { (char)0x30F0, (char)0x3099 };
	decompositionTable.put(key, subst);
	

	key = (char)0x30F9;
	

	subst = new char[] { (char)0x30F1, (char)0x3099 };
	decompositionTable.put(key, subst);
	

	key = (char)0x30FA;
	

	subst = new char[] { (char)0x30F2, (char)0x3099 };
	decompositionTable.put(key, subst);

	key = (char)0x30FE;
	

	subst = new char[] { (char)0x30FD, (char)0x3099 };
	decompositionTable.put(key, subst);
	

	key = (char)0xFB1F;
	

	subst = new char[] { (char)0x05F2, (char)0x05B7 };
	decompositionTable.put(key, subst);
	

	key = (char)0xFB2A;
	

	subst = new char[] { (char)0x05E9, (char)0x05C1 };
	decompositionTable.put(key, subst);
	

	key = (char)0xFB2B;
	

	subst = new char[] { (char)0x05E9, (char)0x05C2 };
	decompositionTable.put(key, subst);

	key = (char)0xFB2C;
	

	subst = new char[] { (char)0x05E9, (char)0x05BC, (char)0x05C1 };
	decompositionTable.put(key, subst);
	

	key = (char)0xFB2D;
	

	subst = new char[] { (char)0x05E9, (char)0x05BC, (char)0x05C2 };
	decompositionTable.put(key, subst);
	

	key = (char)0xFB2E;
	

	subst = new char[] { (char)0x05D0, (char)0x05B7 };
	decompositionTable.put(key, subst);
	

	key = (char)0xFB2F;
	

	subst = new char[] { (char)0x05D0, (char)0x05B8 };
	decompositionTable.put(key, subst);

	key = (char)0xFB30;
	

	subst = new char[] { (char)0x05D0, (char)0x05BC };
	decompositionTable.put(key, subst);
	

	key = (char)0xFB31;
	

	subst = new char[] { (char)0x05D1, (char)0x05BC };
	decompositionTable.put(key, subst);
	

	key = (char)0xFB32;
	

	subst = new char[] { (char)0x05D2, (char)0x05BC };
	decompositionTable.put(key, subst);
	

	key = (char)0xFB33;
	

	subst = new char[] { (char)0x05D3, (char)0x05BC };
	decompositionTable.put(key, subst);

	key = (char)0xFB34;
	

	subst = new char[] { (char)0x05D4, (char)0x05BC };
	decompositionTable.put(key, subst);
	

	key = (char)0xFB35;
	

	subst = new char[] { (char)0x05D5, (char)0x05BC };
	decompositionTable.put(key, subst);
	

	key = (char)0xFB36;
	

	subst = new char[] { (char)0x05D6, (char)0x05BC };
	decompositionTable.put(key, subst);
	

	key = (char)0xFB38;
	

	subst = new char[] { (char)0x05D8, (char)0x05BC };
	decompositionTable.put(key, subst);

	key = (char)0xFB39;
	

	subst = new char[] { (char)0x05D9, (char)0x05BC };
	decompositionTable.put(key, subst);
	

	key = (char)0xFB3A;
	

	subst = new char[] { (char)0x05DA, (char)0x05BC };
	decompositionTable.put(key, subst);
	

	key = (char)0xFB3B;
	

	subst = new char[] { (char)0x05DB, (char)0x05BC };
	decompositionTable.put(key, subst);
	

	key = (char)0xFB3C;
	

	subst = new char[] { (char)0x05DC, (char)0x05BC };
	decompositionTable.put(key, subst);

	key = (char)0xFB3E;
	

	subst = new char[] { (char)0x05DE, (char)0x05BC };
	decompositionTable.put(key, subst);
	

	key = (char)0xFB40;
	

	subst = new char[] { (char)0x05E0, (char)0x05BC };
	decompositionTable.put(key, subst);
	

	key = (char)0xFB41;
	

	subst = new char[] { (char)0x05E1, (char)0x05BC };
	decompositionTable.put(key, subst);
	

	key = (char)0xFB43;
	

	subst = new char[] { (char)0x05E3, (char)0x05BC };
	decompositionTable.put(key, subst);

	key = (char)0xFB44;
	

	subst = new char[] { (char)0x05E4, (char)0x05BC };
	decompositionTable.put(key, subst);
	

	key = (char)0xFB46;
	

	subst = new char[] { (char)0x05E6, (char)0x05BC };
	decompositionTable.put(key, subst);
	

	key = (char)0xFB47;
	

	subst = new char[] { (char)0x05E7, (char)0x05BC };
	decompositionTable.put(key, subst);
	

	key = (char)0xFB48;
	

	subst = new char[] { (char)0x05E8, (char)0x05BC };
	decompositionTable.put(key, subst);

	key = (char)0xFB49;
	

	subst = new char[] { (char)0x05E9, (char)0x05BC };
	decompositionTable.put(key, subst);
	

	key = (char)0xFB4A;
	

	subst = new char[] { (char)0x05EA, (char)0x05BC };
	decompositionTable.put(key, subst);
	

	key = (char)0xFB4B;
	

	subst = new char[] { (char)0x05D5, (char)0x05B9 };
	decompositionTable.put(key, subst);
	

	key = (char)0xFB4C;
	

	subst = new char[] { (char)0x05D1, (char)0x05BF };
	decompositionTable.put(key, subst);

	key = (char)0xFB4D;
	

	subst = new char[] { (char)0x05DB, (char)0x05BF };
	decompositionTable.put(key, subst);
	

	key = (char)0xFB4E;
	

	subst = new char[] { (char)0x05E4, (char)0x05BF };
	decompositionTable.put(key, subst);
    }
}
