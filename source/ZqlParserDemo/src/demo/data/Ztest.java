/*
 * This file is part of Zql.
 *
 * Zql is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Zql is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Zql.  If not, see <http://www.gnu.org/licenses/>.
 */

package demo.data;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.StringBufferInputStream;
import java.util.Vector;

import org.gibello.zql.ZStatement;
import org.gibello.zql.ZqlParser;

public class Ztest {

	public static void main(String args[]) {
		try {

			ZqlParser p = null;
			StringBufferInputStream si = new StringBufferInputStream("SELECT * from NPA where x =1 and y=4;");
			p = new ZqlParser(new DataInputStream(si));
			

			// Read all SQL statements from input
			Vector v = p.readStatements();

			for (int i = 0; i < v.size(); i++) {
				ZStatement st = (ZStatement) v.elementAt(i);
				System.out.println(st.toString());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

};
