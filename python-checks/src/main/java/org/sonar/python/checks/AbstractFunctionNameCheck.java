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
import java.util.regex.Pattern;
import org.sonar.check.RuleProperty;
import org.sonar.python.PythonCheck;
import org.sonar.python.api.PythonGrammar;

public abstract class AbstractFunctionNameCheck extends PythonCheck {

  private static final String DEFAULT = "^[a-z_][a-z0-9_]{2,}$";
  private static final String MESSAGE = "Rename %s \"%s\" to match the regular expression %s.";

  @RuleProperty(
    key = "format",
    defaultValue = "" + DEFAULT)
  public String format = DEFAULT;
  private Pattern pattern = null;

  @Override
  public void init() {
    pattern = Pattern.compile(format);
    subscribeTo(PythonGrammar.FUNCDEF);
  }

  @Override
  public void visitNode(AstNode astNode) {
    if (!shouldCheckFunctionDeclaration(astNode)) {
      return;
    }
    AstNode nameNode = astNode.getFirstChild(PythonGrammar.FUNCNAME);
    String name = nameNode.getTokenValue();
    if (!pattern.matcher(name).matches()) {
      String message = String.format(MESSAGE, typeName(), name, this.format);
      addIssue(nameNode, message);
    }
  }

  public abstract String typeName();

  public abstract boolean shouldCheckFunctionDeclaration(AstNode astNode);

}
