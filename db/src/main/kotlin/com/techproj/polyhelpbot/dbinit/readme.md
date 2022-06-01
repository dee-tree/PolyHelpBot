
# `.json` DB file configuration

Root object consists of fields:
* Array `places`
* Array `questions`
* Array `states`
* `initialStateId`: string
****
1. `places` is an array of objects identified locations

View:`{`  
`"address": string,`  
//TODO REMOVE IT`"help": string,`  
// TODO REMOVE IT`"labels": array<string>,`  
`"location": *location object*,`  
`"name": string,`  
//TODO REMOVE IT: `"keywords": array<string>`  
`}`  

**location object** `{`  
`"latitude": double,`  
`"longitude": double`  
`}`  
****
2. `questions`

View:
`{`  
`"question": string,`  
`"keywords": array<string>,`  
`"answer": *answer object*`  
`}` 

**answer object** `{`  
`"location": string? (name in *location object*),`  
`"text": string?`  
`}`  
 
****
3. `states`

View:
`{`  
`"id": string,`  
`"description": string (optional),`  
`"variants": array<*state connection*>`  
`}`  

**state connection** `{`  
`"stateId: string,`  
`"variant": string,`  
`"answerId": string (question field in questions)`
`}`  