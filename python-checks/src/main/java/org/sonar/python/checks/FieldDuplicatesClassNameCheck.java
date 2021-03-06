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
package org.sonar.python.checks;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Token;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.python.PythonCheck;
import org.sonar.python.api.PythonGrammar;
import org.sonar.squidbridge.annotations.ActivatedByDefault;
import org.sonar.squidbridge.annotations.SqaleConstantRemediation;

import java.util.List;

@Rule(
    key = FieldDuplicatesClassNameCheck.CHECK_KEY,
    priority = Priority.MAJOR,
    name = "A field should not duplicate the name of its containing class",
    tags = Tags.BRAIN_OVERLOAD
)
@SqaleConstantRemediation("10min")
@ActivatedByDefault
public class FieldDuplicatesClassNameCheck extends PythonCheck {

  public static final String CHECK_KEY = "S1700";

  private static final String MESSAGE = "Rename field \"%s\"";

  @Override
  public void init() {
    subscribeTo(PythonGrammar.CLASSDEF);
  }

  @Override
  public void visitNode(AstNode astNode) {
    if (!CheckUtils.classHasInheritance(astNode)) {
      List<Token> allFields = new NewSymbolsAnalyzer().getClassFields(astNode);
      String className = astNode.getFirstChild(PythonGrammar.CLASSNAME).getTokenValue();

      for (Token name : allFields) {
        if (className.equalsIgnoreCase(name.getValue())) {
          addIssue(name, String.format(MESSAGE, name.getValue()))
            .secondary(astNode.getFirstChild(PythonGrammar.CLASSNAME), "Class declaration");
        }
      }
    }
  }

}
