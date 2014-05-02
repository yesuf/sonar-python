/*
 * SonarQube Python Plugin
 * Copyright (C) 2011 SonarSource and Waleri Enns
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.python.parser.compound_statements;

import org.junit.Before;
import org.junit.Test;
import org.sonar.python.api.PythonGrammarBis;
import org.sonar.python.parser.PythonTestUtils;
import org.sonar.python.parser.RuleTest;

import static org.sonar.sslr.tests.Assertions.assertThat;

public class WithStatementTest extends RuleTest {

  @Before
  public void init() {
    setRootRule(PythonGrammarBis.WITH_STMT);
  }

  @Test
  public void ok() {
    p.getGrammar().rule(PythonGrammarBis.SUITE).mock();
    p.getGrammar().rule(PythonGrammarBis.WITH_ITEM).mock();

    assertThat(p).matches("with WITH_ITEM , WITH_ITEM : SUITE");
    assertThat(p).matches("with WITH_ITEM : SUITE");
  }

  @Test
  public void realLife() {
    assertThat(p).matches(PythonTestUtils.appendNewLine("with A() as a : pass"));
    assertThat(p).matches(PythonTestUtils.appendNewLine("with A() as a, B() as b : pass"));
  }

}
