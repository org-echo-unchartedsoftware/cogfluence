/*
 * Copyright 2013-2016 Uncharted Software Inc.
 *
 *  Property of Uncharted(TM), formerly Oculus Info Inc.
 *  https://uncharted.software/
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.oculusinfo.ml.spark;

import com.oculusinfo.ml.Instance;
import java.io.Serializable;
import scala.Tuple2;

/** Interface for parsing raw data into Instance objects for Spark processing. */
public interface SparkInstanceParser extends Serializable {

  /**
   * Parse a line of input into a key-value tuple containing the instance ID and Instance object.
   *
   * @param line The raw input line to parse
   * @return A tuple containing (instanceId, Instance) or null if parsing fails
   * @throws Exception if parsing fails
   */
  Tuple2<String, Instance> parse(String line) throws Exception;

  /** Alias for parse() to support legacy code using call(). */
  default Tuple2<String, Instance> call(String line) throws Exception {
    return parse(line);
  }
}
