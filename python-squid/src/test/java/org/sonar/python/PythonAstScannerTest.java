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

import org.junit.Test;
import org.sonar.python.api.PythonMetric;
import org.sonar.squidbridge.api.SourceFile;

import static org.assertj.core.api.Assertions.assertThat;

public class PythonAstScannerTest {

  @Test
  public void statements() {
    SourceFile file = PythonAstScanner.scanSingleFile("src/test/resources/metrics/statements.py");
    assertThat(file.getInt(PythonMetric.STATEMENTS)).isEqualTo(1);
  }

  @Test
  public void functions() {
    SourceFile file = PythonAstScanner.scanSingleFile("src/test/resources/metrics/functions.py");
    assertThat(file.getInt(PythonMetric.FUNCTIONS)).isEqualTo(1);
  }

  @Test
  public void classes() {
    SourceFile file = PythonAstScanner.scanSingleFile("src/test/resources/metrics/classes.py");
    assertThat(file.getInt(PythonMetric.CLASSES)).isEqualTo(1);
  }

  @Test
  public void complexity() {
    SourceFile file = PythonAstScanner.scanSingleFile("src/test/resources/metrics/complexity.py");
    assertThat(file.getInt(PythonMetric.COMPLEXITY)).isEqualTo(7);
  }

}
