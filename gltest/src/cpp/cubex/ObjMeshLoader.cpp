/*
 * ObjLoader.cpp
 *
 *  Created on: 17.06.2013
 *      Author: il
 */

#include <stdio.h>
#include <stdlib.h>
#include <string>
#include <vector>

#include "ObjMeshLoader.h"

using namespace std;

namespace cubex
{

	ObjMeshLoader::ObjMeshLoader()
	{
	}

	string readLine(FILE* file)
	{
		char ch = 0;
		string res = "";
		bool eoln = false;
		while (!eoln)
		{
			ch = fgetc(file);
			if (ch == 13)
			{
				char nl2 = fgetc(file);
				if (nl2 != 10) ungetc(nl2, file);
				eoln = true;
			}
			else if (ch == 10)
			{
				eoln = true;
			}
			else if (feof(file))
			{
				eoln = true;
			}
			else
			{
				res += ch;
			}
		}

		return res;
	}

	vector<string> parseTokens(const string& line)
	{
		vector<string> res;
		if (line.length() == 0) return res;
		int index = 0;

		res.push_back("");

		// Skipping spaces on the start of the line
		while (index < line.length() && (line[index] == ' ' || line[index] == '\t'))
		{
			index++;
		}

		char current = 0;

		bool previousEmpty = false;
		while (index < line.length())
		{
			current = line[index];
			bool currentEmpty = (current == ' ' || current == '\t');
			if (!previousEmpty && !currentEmpty)
			{
				res[res.size() - 1] += current;
			}
			else if (!previousEmpty && currentEmpty)
			{
				// Do nothing, just relax
			}
			else if (previousEmpty && !currentEmpty)
			{
				res.push_back(string("") + current);
			}
			previousEmpty = currentEmpty;
			index++;
		}

		return res;
	}

	vector<string> parseSlashed(string slashed)
	{
		vector<string> res;
		res.push_back("");

		for (int i = 0; i < slashed.size(); i++)
		{
			if (slashed[i] != '/')
			{
				res[res.size() - 1] += slashed[i];
			}
			else
			{
				res.push_back("");
			}
		}

		return res;
	}

	Mesh ObjMeshLoader::createMeshFromFile(const string& fileName)
	{
		FILE* f = fopen(fileName.c_str(), "r");
		if (f == NULL)
		{
			throw CubexException(__FILE__, __LINE__, string("Can't open the file ") + fileName);
		}

		Mesh res;

		bool error = false;
		string errorMessage;


		int lineNumber = 1;

		while (!feof(f))
		{
			string line = readLine(f);

			vector<string> tokens = parseTokens(line);

			if (tokens.size() > 0)
			{
				if (tokens[0] == "v")
				{
					// Vertex instruction

					if (tokens.size() != 4)
					{
						error = true;
						errorMessage = "Incorrect number of tokens in vertex declaration at line " + lineNumber;
						break;
					}

					float x = strtod(tokens[1].c_str(), NULL);
					float y = strtod(tokens[2].c_str(), NULL);
					float z = strtod(tokens[3].c_str(), NULL);

					res.addVertex(glm::vec3(x, y, z));
				}
				else if (tokens[0] == "vt")
				{
					// Texture coordinates instruction

					if (tokens.size() != 3)
					{
						error = true;
						errorMessage = "Incorrect number of tokens in vertex coordinates declaration at line " + lineNumber;
						break;
					}

					float u = strtod(tokens[1].c_str(), NULL);
					float v = strtod(tokens[2].c_str(), NULL);

					res.addTextureCoords(glm::vec2(u, v));
				}
				else if (tokens[0] == "vn")
				{
					// Normal instruction

					if (tokens.size() != 4)
					{
						error = true;
						errorMessage = "Incorrect number of tokens in normal declaration at line " + lineNumber;
						break;
					}

					float x = strtod(tokens[1].c_str(), NULL);
					float y = strtod(tokens[2].c_str(), NULL);
					float z = strtod(tokens[3].c_str(), NULL);

					res.addNormal(glm::vec3(x, y, z));
				}
				else if (tokens[0] == "f")
				{
					// Face instruction

					if (tokens.size() == 4)
					{
						// Triangular face

						vector<string> indices[3];

						indices[0] = parseSlashed(tokens[1].c_str());
						indices[1] = parseSlashed(tokens[2].c_str());
						indices[2] = parseSlashed(tokens[3].c_str());

						if (indices[0].size() == 1)
						{
							// No texture coords, no normals

							if (indices[1].size() == 1 && indices[2].size() == 1)
							{
								int v1 = atoi(indices[0][0].c_str()) - 1;
								int v2 = atoi(indices[1][0].c_str()) - 1;
								int v3 = atoi(indices[2][0].c_str()) - 1;

								res.addFace(Face::fromVertices(v1, v2, v3));
							}
							else
							{
								error = true;
								errorMessage = "Incorrect number of indices in face declaration at line" + lineNumber;
								break;
							}
						}
						else if (indices[0].size() == 2)
						{
							// Texture coords only

							if (indices[1].size() == 2 && indices[2].size() == 2)
							{
								int v1 = atoi(indices[0][0].c_str()) - 1;
								int v2 = atoi(indices[1][0].c_str()) - 1;
								int v3 = atoi(indices[2][0].c_str()) - 1;

								int vt1 = atoi(indices[0][1].c_str()) - 1;
								int vt2 = atoi(indices[1][1].c_str()) - 1;
								int vt3 = atoi(indices[2][1].c_str()) - 1;

								res.addFace(Face::fromVerticesAndTextureCoords(v1, v2, v3, vt1, vt2, vt3));
							}
							else
							{
								error = true;
								errorMessage = "Incorrect number of indices in face declaration at line" + lineNumber;
								break;
							}
						}
						else if (indices[0].size() == 3)
						{
							if (indices[1].size() == 3 && indices[2].size() == 3)
							{
								if (indices[0][1] == "" && indices[1][1] == "" && indices[2][1] == "")
								{
									// Normals only (no texture coordinates)

									int v1 = atoi(indices[0][0].c_str()) - 1;
									int v2 = atoi(indices[1][0].c_str()) - 1;
									int v3 = atoi(indices[2][0].c_str()) - 1;

									int vn1 = atoi(indices[0][2].c_str()) - 1;
									int vn2 = atoi(indices[1][2].c_str()) - 1;
									int vn3 = atoi(indices[2][2].c_str()) - 1;

									res.addFace(Face::fromVerticesAndNormals(v1, v2, v3, vn1, vn2, vn3));
								}
								else if (indices[0][1] != "" && indices[1][1] != "" && indices[2][1] != "")
								{
									// Normals and texture coordinates

									int v1 = atoi(indices[0][0].c_str()) - 1;
									int v2 = atoi(indices[1][0].c_str()) - 1;
									int v3 = atoi(indices[2][0].c_str()) - 1;

									int vt1 = atoi(indices[0][1].c_str()) - 1;
									int vt2 = atoi(indices[1][1].c_str()) - 1;
									int vt3 = atoi(indices[2][1].c_str()) - 1;

									int vn1 = atoi(indices[0][2].c_str()) - 1;
									int vn2 = atoi(indices[1][2].c_str()) - 1;
									int vn3 = atoi(indices[2][2].c_str()) - 1;

									res.addFace(Face::fromVerticesAndTextureCoordsAndNormals(v1, v2, v3, vt1, vt2, vt3, vn1, vn2, vn3));
								}
								else
								{
									error = true;
									errorMessage = "Incorrect number of indices in face declaration at line" + lineNumber;
									break;
								}

							}
							else
							{
								error = true;
								errorMessage = "Incorrect number of indices in face declaration at line" + lineNumber;
								break;
							}
						}
					}
					else if (tokens.size() == 5)
					{
						// Quad face (loaded as 2 triangle faces)

						vector<string> indices[4];
						indices[0] = parseSlashed(tokens[1].c_str());
						indices[1] = parseSlashed(tokens[2].c_str());
						indices[2] = parseSlashed(tokens[3].c_str());
						indices[3] = parseSlashed(tokens[4].c_str());

						if (indices[0].size() == 1)
						{
							// No texture coords, no normals

							if (indices[1].size() == 1 && indices[2].size() == 1 && indices[3].size() == 1)
							{
								int v1 = atoi(indices[0][0].c_str()) - 1;
								int v2 = atoi(indices[1][0].c_str()) - 1;
								int v3 = atoi(indices[2][0].c_str()) - 1;
								int v4 = atoi(indices[3][0].c_str()) - 1;

								res.addFace(Face::fromVertices(v1, v2, v3));
								res.addFace(Face::fromVertices(v3, v4, v1));
							}
							else
							{
								error = true;
								errorMessage = "Incorrect number of indices in face declaration at line" + lineNumber;
								break;
							}
						}
						else if (indices[0].size() == 2)
						{
							// Texture coords only

							if (indices[1].size() == 2 && indices[2].size() == 2 && indices[3].size() == 2)
							{
								int v1 = atoi(indices[0][0].c_str()) - 1;
								int v2 = atoi(indices[1][0].c_str()) - 1;
								int v3 = atoi(indices[2][0].c_str()) - 1;
								int v4 = atoi(indices[3][0].c_str()) - 1;

								int vt1 = atoi(indices[0][1].c_str()) - 1;
								int vt2 = atoi(indices[1][1].c_str()) - 1;
								int vt3 = atoi(indices[2][1].c_str()) - 1;
								int vt4 = atoi(indices[3][1].c_str()) - 1;

								res.addFace(Face::fromVerticesAndTextureCoords(v1, v2, v3, vt1, vt2, vt3));
								res.addFace(Face::fromVerticesAndTextureCoords(v3, v4, v1, vt3, vt4, vt1));
							}
							else
							{
								error = true;
								errorMessage = "Incorrect number of indices in face declaration at line" + lineNumber;
								break;
							}
						}
						else if (indices[0].size() == 3)
						{
							if (indices[1].size() == 3 && indices[2].size() == 3 && indices[3].size() == 3)
							{
								if (indices[0][1] == "" && indices[1][1] == "" && indices[2][1] == "" && indices[3][1] == "")
								{
									// Normals only (no texture coordinates)

									int v1 = atoi(indices[0][0].c_str()) - 1;
									int v2 = atoi(indices[1][0].c_str()) - 1;
									int v3 = atoi(indices[2][0].c_str()) - 1;
									int v4 = atoi(indices[3][0].c_str()) - 1;

									int vn1 = atoi(indices[0][2].c_str()) - 1;
									int vn2 = atoi(indices[1][2].c_str()) - 1;
									int vn3 = atoi(indices[2][2].c_str()) - 1;
									int vn4 = atoi(indices[3][2].c_str()) - 1;

									res.addFace(Face::fromVerticesAndNormals(v1, v2, v3, vn1, vn2, vn3));
									res.addFace(Face::fromVerticesAndNormals(v3, v4, v1, vn3, vn4, vn1));
								}
								else if (indices[0][1] != "" && indices[1][1] != "" && indices[2][1] != "")
								{
									// Normals and texture coordinates

									int v1 = atoi(indices[0][0].c_str()) - 1;
									int v2 = atoi(indices[1][0].c_str()) - 1;
									int v3 = atoi(indices[2][0].c_str()) - 1;
									int v4 = atoi(indices[3][0].c_str()) - 1;

									int vt1 = atoi(indices[0][1].c_str()) - 1;
									int vt2 = atoi(indices[1][1].c_str()) - 1;
									int vt3 = atoi(indices[2][1].c_str()) - 1;
									int vt4 = atoi(indices[3][1].c_str()) - 1;

									int vn1 = atoi(indices[0][2].c_str()) - 1;
									int vn2 = atoi(indices[1][2].c_str()) - 1;
									int vn3 = atoi(indices[2][2].c_str()) - 1;
									int vn4 = atoi(indices[3][2].c_str()) - 1;

									res.addFace(Face::fromVerticesAndTextureCoordsAndNormals(v1, v2, v3, vt1, vt2, vt3, vn1, vn2, vn3));
									res.addFace(Face::fromVerticesAndTextureCoordsAndNormals(v3, v4, v1, vt3, vt4, vt1, vn3, vn4, vn1));
								}
								else
								{
									error = true;
									errorMessage = "Incorrect number of indices in face declaration at line" + lineNumber;
									break;
								}

							}
							else
							{
								error = true;
								errorMessage = "Incorrect number of indices in face declaration at line" + lineNumber;
								break;
							}
						}

					}
					else
					{
						error = true;
						errorMessage = "Incorrect number of tokens in face declaration at line " + lineNumber;
						break;
					}
				}
				else
				{
					// Unrecognized line
				}
			}

			lineNumber ++;
		}

		fclose(f);

		if (error)
		{
			throw CubexException(__FILE__, __LINE__, errorMessage);
		}

		return res;
	}

	ObjMeshLoader::~ObjMeshLoader()
	{
		// TODO Auto-generated destructor stub
	}

} /* namespace cubex */
