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

package controller;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.StringBufferInputStream;
import java.util.Vector;

import org.gibello.zql.ParseException;
import org.gibello.zql.ZExp;
import org.gibello.zql.ZExpression;
import org.gibello.zql.ZFromItem;
import org.gibello.zql.ZInsert;
import org.gibello.zql.ZQuery;
import org.gibello.zql.ZSelectItem;
import org.gibello.zql.ZStatement;
import org.gibello.zql.ZqlParser;

public class Main {

	@SuppressWarnings("unchecked")
	public static void main(String args[]) {
		final String query = "SELECT a.ASSMT_NO, b.LINK_PARAM, c.EXPL AS LINK_PG, (SELECT count() FROM GRAASPST t WHERE t.ASSMT_NO = a.ASSMT_NO AND t.ROLE != '02') AS PSN_CNT, (SELECT count() FROM GRAASPST t WHERE t.ASSMT_NO = a.ASSMT_NO AND t.ROLE != '02' AND ASSMT_FIN_YN = 'Y') AS PSN_FIN_CNT, (SELECT Avg(assmt_pts) FROM GRAASSMT t WHERE t.ASSMT_NO = a.ASSMT_NO AND t.ASSMT_TGT_SEQ_NO = a.ASSMT_TGT_SEQ_NO) AS ASSMT_PTS_AVG, a.ASSMT_RES, a.ASSMT_RPT_SUB_TITLE FROM GRAASTAT a JOIN GRAASRET b ON b.DELIB_REQ_NO = a.DELIB_REQ_NO JOIN GRTCODDT c ON c.DIV_CD = 'GR013' AND c.CD = b.DELIB_SLCT JOIN CMUSERMT d ON d.USERID = a.REGID WHERE a.ASSMT_NO = :ASSMT_NO ORDER BY a.ASSMT_TGT_SEQ_NO;";
		try {

			ZqlParser parser = new ZqlParser(new ByteArrayInputStream(query.getBytes()));

			// Read all SQL statements from input
			Vector<ZStatement> statements = parser.readStatements();

			for (ZStatement st : statements) {

				// Filtramos solo los SELECTS
				if (!(st instanceof ZQuery)) {
					continue;
				}

				ZQuery q = (ZQuery) st;

				System.out.println("QUERY ANALIZADA:\n" + q);

				System.out.println("\n***SELECT***");
				Vector<ZSelectItem> selectItems = q.getSelect();
				for (ZSelectItem zs : selectItems) {
					System.out.println("[" + zs + "]");
					System.out.println("\t column:" + zs.getColumn());
					System.out.println("\t alias:" + zs.getAlias());
					System.out.println("\t schema:" + zs.getSchema());
					System.out.println("\t aggregate:" + zs.getAggregate());
					System.out.println("\t table:" + zs.getTable());

					if (zs.isExpression()) {
						System.out.println("\t es expression");
						System.out.println("\t expression:" + zs.getExpression());
					}
					if (zs.isWildcard()) {
						// Por ejemplo el *
						System.out.println("\t es Wildcard");
					}
				}

				System.out.println("\n***FROM***");
				Vector<ZFromItem> fromItems = q.getFrom();
				for (ZFromItem zf : fromItems) {
					System.out.println("[" + zf + "]");
					System.out.println("\t column:" + zf.getColumn());
					System.out.println("\t alias:" + zf.getAlias());
					System.out.println("\t schema:" + zf.getSchema());
					System.out.println("\t table:" + zf.getTable());

					if (zf.isWildcard()) {
						// Por ejemplo el *
						System.out.println("\t es Wildcard");
					}
				}

				System.out.println("\n***WHERE***");
				// Puede devolver ZQuery (subquery) o ZExpression
				ZExp where = q.getWhere();

				if (where instanceof ZQuery) {
					ZQuery subq = (ZQuery) where;
					System.out.println("sub-query:" + subq);
				} else if (where instanceof ZExpression) {
					ZExpression vWhere = (ZExpression) where;
					Vector<ZExp> operands = vWhere.getOperands();
					for (ZExp op : operands) {
						System.out.println("a" + op);
					}
				}
			}

		} catch (ParseException e) {
			System.err.println("PARSE EXCEPTION:");
			e.printStackTrace(System.err);
		} catch (Error e) {
			System.err.println("ERROR");
		} catch (Exception e) {
			System.err.println("CLASS" + e.getClass());
			e.printStackTrace(System.err);
		}

	}

};
