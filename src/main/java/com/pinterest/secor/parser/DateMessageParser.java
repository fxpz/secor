/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pinterest.secor.parser;

import java.text.SimpleDateFormat;
import java.util.Date;

import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pinterest.secor.common.SecorConfig;
import com.pinterest.secor.message.Message;

/**
 * DateMessageParser extracts timestamp field (specified by 'message.timestamp.name') 
 *  and the date pattern (specified by 'message.timestamp.input.pattern')
 * 
 * @see http://docs.oracle.com/javase/6/docs/api/java/text/SimpleDateFormat.html
 * 
 * @author Lucas Zago (lucaszago@gmail.com)
 * 
 */
public class DateMessageParser extends MessageParser {
    private static final Logger LOG = LoggerFactory.getLogger(DateMessageParser.class);
    protected static final String defaultDate = "dt=1970-01-01";
    protected static final String defaultHour = "hr=00";
    protected static final String defaultDateFormatter = "yyyy-MM-dd";
    protected static final String defaultHourFormatter = "HH";
    private final boolean mUsingHourly;

    public DateMessageParser(SecorConfig config) {
        super(config);
        mUsingHourly = usingHourly(config);
        LOG.info("UsingHourly: {}", mUsingHourly);
    }

    @Override
    public String[] extractPartitions(Message message) {
        JSONObject jsonObject = (JSONObject) JSONValue.parse(message.getPayload());
        String result[];
        if (mUsingHourly) {
          result = new String[2];
          result[0] = defaultDate;
          result[1] = defaultHour;
        } else {
          result = new String[1];
          result[0] = defaultDate;
        }

        if (jsonObject != null) {
            Object fieldValue = jsonObject.get(mConfig.getMessageTimestampName());
            Object inputPattern = mConfig.getMessageTimestampInputPattern();
            if (fieldValue != null && inputPattern != null) {
                try {
                    SimpleDateFormat inputFormatter = new SimpleDateFormat(inputPattern.toString());
                    SimpleDateFormat outputDateFormatter = new SimpleDateFormat(defaultDateFormatter);
                    SimpleDateFormat outputHourFormatter = new SimpleDateFormat(defaultHourFormatter);
                    Date dateFormat = inputFormatter.parse(fieldValue.toString());
                    if (mUsingHourly) {
                        result[0] = "dt=" + outputDateFormatter.format(dateFormat);
                        result[1] = "hr=" + outputHourFormatter.format(dateFormat);
                    } else {
                        result[0] = "dt=" + outputDateFormatter.format(dateFormat);
                    }
                } catch (Exception e) {
                    if (mUsingHourly) {
                        LOG.warn("Impossible to convert date = {} for the input pattern = {} . Using date default = {}/{}",
                            fieldValue.toString(), inputPattern.toString(), result[0], result[1]);
                    } else { 
                        LOG.warn("Impossible to convert date = {} for the input pattern = {} . Using date default = {}",
                            fieldValue.toString(), inputPattern.toString(), result[0]);
                    }
                }
            }
        }

        return result;
    }

    static boolean usingHourly(SecorConfig config) {
      return config.getBoolean("partitioner.granularity.hour", false);
    }

}
