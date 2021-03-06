/*
 * SonarQube Python Plugin
 * Copyright (C) 2011-2017 SonarSource SA
 * mailto:info AT sonarsource DOT com
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
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.python;

import com.sonar.sslr.api.AstNode;
import org.junit.Test;
import org.sonar.python.PythonCheck.IssueLocation;
import org.sonar.python.PythonCheck.PreciseIssue;
import org.sonar.python.api.PythonGrammar;

import java.io.File;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class PythonCheckTest {

  private static final String FILE = "src/test/resources/file.py";

  @Test
  public void test() throws Exception {
    TestPythonCheck check = new TestPythonCheck (){
      @Override
      public void visitNode(AstNode astNode) {
        AstNode funcName = astNode.getFirstChild(PythonGrammar.FUNCNAME);
        addIssue(funcName, funcName.getTokenValue());
      }
    };

    PythonAstScanner.scanSingleFile(FILE, check);

    List<PreciseIssue> issues = check.getIssues();

    assertThat(issues).hasSize(2);
    PreciseIssue firstIssue = issues.get(0);

    assertThat(firstIssue.cost()).isNull();
    assertThat(firstIssue.file()).isEqualTo(new File(FILE));
    assertThat(firstIssue.secondaryLocations()).isEmpty();

    IssueLocation primaryLocation = firstIssue.primaryLocation();
    assertThat(primaryLocation.message()).isEqualTo("hello");

    assertThat(primaryLocation.startLine()).isEqualTo(1);
    assertThat(primaryLocation.endLine()).isEqualTo(1);
    assertThat(primaryLocation.startLineOffset()).isEqualTo(4);
    assertThat(primaryLocation.endLineOffset()).isEqualTo(9);
  }

  @Test
  public void test_cost() throws Exception {
    TestPythonCheck check = new TestPythonCheck (){
      @Override
      public void visitNode(AstNode astNode) {
        addIssue(astNode.getFirstChild(PythonGrammar.FUNCNAME), "message").withCost(42);
      }
    };

    PythonAstScanner.scanSingleFile(FILE, check);

    PreciseIssue firstIssue = check.getIssues().get(0);
    assertThat(firstIssue.cost()).isEqualTo(42);
  }

  @Test
  public void test_secondary_location() throws Exception {
    TestPythonCheck check = new TestPythonCheck (){
      @Override
      public void visitNode(AstNode astNode) {
        PreciseIssue issue = addIssue(astNode.getFirstChild(PythonGrammar.FUNCNAME), "message")
          .secondary(astNode.getFirstChild(), "def keyword");

        AstNode returnStmt = astNode.getFirstDescendant(PythonGrammar.RETURN_STMT);
        if (returnStmt != null) {
          issue.secondary(returnStmt, "return statement");
        }
      }
    };

    PythonAstScanner.scanSingleFile(FILE, check);

    List<IssueLocation> secondaryLocations = check.getIssues().get(0).secondaryLocations();
    assertThat(secondaryLocations).hasSize(2);

    IssueLocation firstSecondaryLocation = secondaryLocations.get(0);
    IssueLocation secondSecondaryLocation = secondaryLocations.get(1);

    assertThat(firstSecondaryLocation.message()).isEqualTo("def keyword");
    assertThat(firstSecondaryLocation.startLine()).isEqualTo(1);
    assertThat(firstSecondaryLocation.startLineOffset()).isEqualTo(0);
    assertThat(firstSecondaryLocation.endLine()).isEqualTo(1);
    assertThat(firstSecondaryLocation.endLineOffset()).isEqualTo(3);

    assertThat(secondSecondaryLocation.message()).isEqualTo("return statement");
    assertThat(secondSecondaryLocation.startLine()).isEqualTo(3);
    assertThat(secondSecondaryLocation.startLineOffset()).isEqualTo(4);
    assertThat(secondSecondaryLocation.endLine()).isEqualTo(4);
    assertThat(secondSecondaryLocation.endLineOffset()).isEqualTo(5);
  }



  private static class TestPythonCheck extends PythonCheck {

    @Override
    public void init() {
      subscribeTo(PythonGrammar.FUNCDEF);
    }

  }
}
