/*
 * Copyright 2020-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package hu.perit.elasticsearchstudy.db.elasticsearch.table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Getter
@Setter
@ToString
@Document(indexName = UserEntity.INDEX_NAME)
public class UserEntity
{
   public static final String INDEX_NAME = "user";

   public static final String FIELD_FIRSTNAME = "firstname";
   public static final String FIELD_LASTNAME = "lastname";
   public static final String FIELD_EMAIL = "email";

   @Id
   private String uuid;

   @Field(type = FieldType.Text, name = FIELD_FIRSTNAME)
   private String firstName;

   @Field(type = FieldType.Text, name = FIELD_LASTNAME)
   private String lastName;

   @Field(type = FieldType.Text, name = FIELD_EMAIL)
   private String email;
}
