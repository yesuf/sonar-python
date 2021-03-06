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
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.python.PythonCheck;
import org.sonar.python.api.PythonGrammar;
import org.sonar.python.api.PythonPunctuator;
import org.sonar.squidbridge.annotations.ActivatedByDefault;
import org.sonar.squidbridge.annotations.SqaleConstantRemediation;

@Rule(
    key = BackticksUsageCheck.CHECK_KEY,
    priority = Priority.MAJOR,
    name = "Backticks should not be used"
)
@SqaleConstantRemediation("5min")
@ActivatedByDefault
public class BackticksUsageCheck extends PythonCheck {
  public static final String CHECK_KEY = "BackticksUsage";

  @Override
  public void init() {
    subscribeTo(PythonGrammar.ATOM);
  }

  @Override
  public void visitNode(AstNode astNode) {
    if (astNode.hasDirectChildren(PythonPunctuator.BACKTICK)) {
      addIssue(astNode, "Use \"repr\" instead.");
    }
  }

}
