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

package demo.api;

import java.io.ByteArrayInputStream;
import java.io.StringBufferInputStream;
import java.util.Vector;

import org.gibello.zql.ZQuery;
import org.gibello.zql.ZSelectItem;
import org.gibello.zql.ZStatement;
import org.gibello.zql.ZqlParser;

public class Ztest {

	public static void main(String args[]) {
		try {

			ZqlParser p = new ZqlParser();
			StringBufferInputStream si = new StringBufferInputStream("SELECT *, 'otra' SUM(movida) from NPA where x =1 and y=4;");
			p.initParser(si);
			ZStatement st = p.readStatement();
			System.out.println(st.toString()); // Display the statement

			ZQuery q = (ZQuery) st;
			Vector v = q.getSelect();
			for (int i = 0; i < v.size(); i++) {
				ZSelectItem it = (ZSelectItem) v.elementAt(i);
				System.out.print("col=" + it.getColumn() + ",agg=" + it.getAggregate());
				String s = it.getSchema();
				if (s != null)
					System.out.print(",schema=" + s);
				s = it.getTable();
				if (s != null)
					System.out.print(",table=" + s);
				System.out.println();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

};
