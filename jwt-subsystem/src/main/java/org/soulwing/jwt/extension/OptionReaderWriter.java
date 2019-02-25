/*
 * File created on Feb 19, 2019
 *
 * Copyright (c) 2019 Carl Harris, Jr
 * and others as noted
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.soulwing.jwt.extension;

import java.util.Collections;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.jboss.as.controller.SimpleMapAttributeDefinition;
import org.jboss.dmr.ModelNode;
import org.jboss.staxmapper.XMLExtendedStreamWriter;

/**
 * A reader/writer for the option element.
 *
 * @author Carl Harris
 */
class OptionReaderWriter extends AbstractResourceReaderWriter {

  private final SimpleMapAttributeDefinition attribute;

  public OptionReaderWriter(SimpleMapAttributeDefinition attribute) {
    super(Names.OPTION);
    this.attribute = attribute;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void handleAttributes(XMLStreamReader reader)   
      throws XMLStreamException {
    String key = null;
    for (int i = 0, max = reader.getAttributeCount(); i < max; i++) {
      String localName = reader.getAttributeLocalName(i);
      if (Names.KEY.equals(localName)) {
        if (key == null) {
          key = reader.getAttributeValue(i);
        }
        else {
          parser.duplicateAttribute(reader, localName);
        }
      }
      else {
        parser.unexpectedAttribute(reader, i);
      }
    }
    
    if (key == null) {
      parser.missingAttributes(reader, Collections.singleton(Names.KEY));
    }
    
    parser.putAttachment(ParserAttachments.OPTION_KEY, key);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void characters(XMLStreamReader reader, String text)
      throws XMLStreamException {
    String key = parser.removeAttachment(ParserAttachments.OPTION_KEY);
    if (key == null) {
      throw new XMLStreamException("no key for option text");
    }

    ModelNode value = attribute.parse(text, reader.getLocation());
    
    parser.lastOperation().get(Names.OPTIONS).get(key).set(value);
  }


  public void writeOptionResources(XMLExtendedStreamWriter writer,
      ModelNode optionsModel)
      throws XMLStreamException {
    if (optionsModel.isDefined()) {
      ModelNode options = optionsModel.asObject();
      for (String key : options.keys()) {
        ModelNode model = new ModelNode();
        model.add(key);
        model.add(options.get(key));
        writeResource(writer, model);
      }
    }
  }

  @Override
  public void writeResource(XMLExtendedStreamWriter writer, ModelNode node)
      throws XMLStreamException {
    writer.writeStartElement(Names.OPTION);
    String key = node.get(0).asString();
    String value = node.get(1).asString();
    writer.writeAttribute(Names.KEY, key);
    writer.writeCharacters(value);
    writer.writeEndElement();
  }

}
