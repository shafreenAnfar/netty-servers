/*
*  Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing,
*  software distributed under the License is distributed on an
*  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*  KIND, either express or implied.  See the License for the
*  specific language governing permissions and limitations
*  under the License.
*/
package carbonReverseProxy;

//import org.ballerinalang.runtime.message.MessageDataSource;

/**
 * {@code StringValue} represents a string value in Ballerina.
 *
 * @since 0.8.0
 */
public class StringDataSource //implements MessageDataSource
{
//    private String value;
//    private OutputStream outputStream;
//
//    /**
//     * Create a String datasource with a string.
//     *
//     * @param value String value
//     */
//    public StringDataSource(String value) {
//        this.value = value;
//        this.outputStream = null;
//    }
//
//    /**
//     * Create a String datasource with a string and a target output stream.
//     *
//     * @param value         String value
//     * @param outputStream  Target outputstream
//     */
//    public StringDataSource(String value, OutputStream outputStream) {
//        this.value = value;
//        this.outputStream = outputStream;
//    }
//
//    public String getValue() {
//        return value;
//    }
//
//    public void setValue(String value) {
//        this.value = value;
//    }
//
//    @Override
//    public void serializeData(OutputStream outputStream) {
//        try {
//            outputStream.write(this.value.getBytes(Charset.defaultCharset()));
//            outputStream.flush();
////            for (int messagePayload = 0; messagePayload < 1000; messagePayload++) {
////                this.outputStream.write(this.value.getBytes(Charset.defaultCharset()));
////                Thread.sleep(1000);
////            }
////            this.outputStream.write(this.value.getBytes(Charset.defaultCharset()));
//            outputStream.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void setOutputStream(OutputStream outputStream) {
//        this.outputStream = outputStream;
//    }
//
//    @Override
//    public String getMessageAsString() {
//        return this.value;
//    }
//
//    @Override
//    public String getValueAsString(String s) {
//        return null;
//    }
//
//    @Override
//    public String getValueAsString(String s, Map<String, String> map) {
//        return null;
//    }
//
//    @Override
//    public Object getValue(String s) {
//        return null;
//    }
//
//    @Override
//    public Object getDataObject() {
//        return null;
//    }
//
//    @Override
//    public String getContentType() {
//        return null;
//    }
//
//    @Override
//    public void setContentType(String s) {
//
//    }
}
